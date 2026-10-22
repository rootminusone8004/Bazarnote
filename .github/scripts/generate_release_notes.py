#!/usr/bin/env python3
"""
Generate release notes from CHANGELOG.md for GitHub Actions Release pipeline.
"""
import os
import re
import sys
from pathlib import Path


def main():
    repo_root = Path.cwd()
    changelog_path = repo_root / "CHANGELOG.md"
    if not changelog_path.exists():
        repo_root = Path(__file__).resolve().parents[2]
        changelog_path = repo_root / "CHANGELOG.md"

    if not changelog_path.exists():
        print(f"::error::CHANGELOG.md not found at {changelog_path}", file=sys.stderr)
        sys.exit(1)

    changelog_content = changelog_path.read_text(encoding="utf-8")

    # 1. Extract version from CHANGELOG.md (e.g. ## [0.9.1] - ...)
    match = re.search(r'^##\s*\[([0-9]+(?:\.[0-9]+)*)\]', changelog_content, re.MULTILINE)
    if not match:
        print("::error::Could not find version pattern '## [X.Y.Z]' in CHANGELOG.md", file=sys.stderr)
        sys.exit(1)

    changelog_version = match.group(1)

    # Respect tag push if ref is refs/tags/v*
    github_ref = os.environ.get("GITHUB_REF", "")
    if github_ref.startswith("refs/tags/v"):
        tag = github_ref.replace("refs/tags/", "")
        version = tag.lstrip("v")
    else:
        version = changelog_version
        tag = f"v{version}"

    release_title = f"Bazarnote {tag}"

    # Compute integer versionCode from semantic version (major * 10000 + minor * 100 + patch)
    try:
        parts = [int(p) for p in version.split(".")]
        major = parts[0] if len(parts) > 0 else 0
        minor = parts[1] if len(parts) > 1 else 0
        patch = parts[2] if len(parts) > 2 else 0
        version_code = major * 10000 + minor * 100 + patch
    except Exception:
        version_code = 901

    # 2. Extract release notes for this version from CHANGELOG.md
    pattern = rf'##\s*\[{re.escape(version)}\][^\n]*\n(.*?)(?=\n##\s*\[|\Z)'
    notes_match = re.search(pattern, changelog_content, re.DOTALL)
    notes = notes_match.group(1).strip() if notes_match else ""

    # 3. Create release body encouraging reading CHANGELOG.md
    release_body = (
        f"### Bazarnote {tag}\n\n"
        f"{notes}\n\n"
        "---\n"
        "📖 See [CHANGELOG.md](CHANGELOG.md) for full details and change history.\n"
    )

    body_file = repo_root / "release_body.md"
    body_file.write_text(release_body, encoding="utf-8")

    # 4. Export outputs for GitHub Actions
    github_output = os.environ.get("GITHUB_OUTPUT")
    if github_output:
        with open(github_output, "a", encoding="utf-8") as f:
            f.write(f"tag_name={tag}\n")
            f.write(f"version={version}\n")
            f.write(f"version_code={version_code}\n")
            f.write(f"release_title={release_title}\n")

    print(f"Version: {version}")
    print(f"Release Tag: {tag}")
    print(f"Version Code: {version_code}")
    print(f"Release Title: {release_title}")
    print(f"Release Body written to: {body_file}")


if __name__ == "__main__":
    main()
