#!/usr/bin/env python3
"""Compile Jaku and run exact-output UI cases from the Markdown test plan."""

from __future__ import annotations

import argparse
import re
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path


CASE_PATTERN = re.compile(
    r"^## (?P<name>[^\n]+)\n\n"
    r"Aim: (?P<aim>[^\n]+)\n\n"
    r"### Input\n\n```text\n(?P<input>.*?)\n```\n\n"
    r"### Expected output\n\n```text\n(?P<expected>.*?)\n```"
    r"(?=\n\n## |\s*\Z)",
    re.MULTILINE | re.DOTALL,
)


@dataclass(frozen=True)
class TestCase:
    """One chatbot session and the exact console output it should produce."""

    name: str
    aim: str
    user_input: str
    expected_output: str


def find_project_root() -> Path:
    """Return the nearest parent directory containing Jaku's Java sources."""
    candidates = (Path.cwd(), *Path.cwd().parents, *Path(__file__).resolve().parents)
    for candidate in candidates:
        if (candidate / "src/main/java/Jaku.java").is_file():
            return candidate
    raise RuntimeError("Could not find a project root containing src/main/java/Jaku.java")


def parse_test_plan(plan_path: Path) -> list[TestCase]:
    """Parse test cases from the plan's documented Markdown structure."""
    plan_text = plan_path.read_text(encoding="utf-8")
    cases = [
        TestCase(
            match.group("name"),
            match.group("aim"),
            match.group("input"),
            match.group("expected"),
        )
        for match in CASE_PATTERN.finditer(plan_text)
    ]
    if not cases:
        raise ValueError(f"No test cases found in {plan_path}")
    return cases


def normalize_output(output: str) -> str:
    """Normalize platform line endings without hiding whitespace differences."""
    return output.replace("\r\n", "\n").rstrip("\n")


def decode_expected_output(output: str) -> str:
    """Restore visible markers used for trailing spaces in Markdown fixtures."""
    return normalize_output(output.replace("␠", " "))


def verify_java_25(project_root: Path) -> None:
    """Fail with a useful message unless javac reports Java 25."""
    result = subprocess.run(
        ["javac", "-version"],
        cwd=project_root,
        capture_output=True,
        text=True,
        check=False,
    )
    version_output = (result.stdout + result.stderr).strip()
    if result.returncode != 0 or not version_output.startswith("javac 25"):
        raise RuntimeError(f"Java 25 is required; found: {version_output or 'javac unavailable'}")


def compile_sources(project_root: Path, classes_directory: Path) -> None:
    """Compile all production Java sources with warnings treated as errors."""
    source_files = sorted((project_root / "src/main/java").glob("*.java"))
    if not source_files:
        raise RuntimeError("No Java source files found in src/main/java")
    result = subprocess.run(
        [
            "javac",
            "-Xlint:all",
            "-Werror",
            "-d",
            str(classes_directory),
            *(str(source_file) for source_file in source_files),
        ],
        cwd=project_root,
        capture_output=True,
        text=True,
        check=False,
    )
    if result.returncode != 0:
        details = (result.stdout + result.stderr).strip()
        raise RuntimeError(f"Compilation failed:\n{details}")


def run_case(project_root: Path, classes_directory: Path, case: TestCase) -> str:
    """Run one fresh Jaku session and return its standard output."""
    result = subprocess.run(
        ["java", "-cp", str(classes_directory), "Jaku"],
        cwd=project_root,
        input=case.user_input + "\n",
        capture_output=True,
        text=True,
        timeout=10,
        check=False,
    )
    if result.returncode != 0:
        raise RuntimeError(
            f"{case.name} exited with status {result.returncode}:\n{result.stderr.strip()}"
        )
    if result.stderr:
        raise RuntimeError(f"{case.name} wrote to standard error:\n{result.stderr.rstrip()}")
    return normalize_output(result.stdout)


def print_transcript(case: TestCase, actual_output: str) -> None:
    """Print the commands and application output for a completed test case."""
    print(f"[{case.name}] {case.aim}")
    print("Input:")
    print(case.user_input)
    print("Output:")
    print(actual_output)


def main() -> int:
    """Run all planned UI cases, stopping immediately on the first failure."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--plan",
        type=Path,
        default=Path("test/ui-test-plan.md"),
        help="test plan path relative to the project root",
    )
    args = parser.parse_args()

    try:
        project_root = find_project_root()
        plan_path = args.plan if args.plan.is_absolute() else project_root / args.plan
        test_cases = parse_test_plan(plan_path)
        verify_java_25(project_root)
        with tempfile.TemporaryDirectory(prefix="jaku-ui-tests-") as temp_directory:
            classes_directory = Path(temp_directory)
            compile_sources(project_root, classes_directory)
            for case in test_cases:
                actual_output = run_case(project_root, classes_directory, case)
                print_transcript(case, actual_output)
                expected_output = decode_expected_output(case.expected_output)
                if actual_output != expected_output:
                    print("Expected:", file=sys.stderr)
                    print(expected_output, file=sys.stderr)
                    print("Actual:", file=sys.stderr)
                    print(actual_output, file=sys.stderr)
                    print(f"FAIL: {case.name}", file=sys.stderr)
                    return 1
                print(f"PASS: {case.name}\n")
    except (OSError, RuntimeError, ValueError, subprocess.TimeoutExpired) as error:
        print(f"ERROR: {error}", file=sys.stderr)
        return 1

    print(f"PASS: {len(test_cases)} UI test case(s)")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
