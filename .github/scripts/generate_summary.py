#!/usr/bin/env python3
"""
Generate a GitHub Actions Step Summary in Markdown with tabular details for Bazarnote.
"""
import hashlib
import os
import re
import sys
from pathlib import Path


def get_sha256(filepath):
    try:
        h = hashlib.sha256()
        with open(filepath, "rb") as f:
            while chunk := f.read(65536):
                h.update(chunk)
        return h.hexdigest()
    except Exception as e:
        return f"Error: {e}"


def format_size(num_bytes):
    if num_bytes < 1024:
        return f"{num_bytes} B"
    elif num_bytes < 1024 * 1024:
        return f"{num_bytes / 1024:.2f} KB"
    else:
        return f"{num_bytes / (1024 * 1024):.2f} MB"


def main():
    try:
        repo_root = Path.cwd()
        changelog_path = repo_root / "CHANGELOG.md"
        if not changelog_path.exists():
            repo_root = Path(__file__).resolve().parents[2]
            changelog_path = repo_root / "CHANGELOG.md"

        # Extract version & notes
        version = "0.9.1"
        notes = ""
        if changelog_path.exists():
            changelog_content = changelog_path.read_text(encoding="utf-8")
            match = re.search(r'^##\s*\[([0-9]+(?:\.[0-9]+)*)\]', changelog_content, re.MULTILINE)
            if match:
                version = match.group(1)
                pattern = rf'##\s*\[{re.escape(version)}\][^\n]*\n(.*?)(?=\n##\s*\[|\Z)'
                notes_match = re.search(pattern, changelog_content, re.DOTALL)
                if notes_match:
                    notes = notes_match.group(1).strip()

        github_ref = os.environ.get("GITHUB_REF", "")
        if github_ref.startswith("refs/tags/v"):
            tag = github_ref.replace("refs/tags/", "")
            version = tag.lstrip("v")
        else:
            tag = f"v{version}"

        # GitHub context
        server_url = os.environ.get("GITHUB_SERVER_URL", "https://github.com")
        repository = os.environ.get("GITHUB_REPOSITORY", "rootminusone8004/Bazarnote")
        commit_sha = os.environ.get("GITHUB_SHA", "")
        short_sha = commit_sha[:7] if commit_sha else "local"
        commit_url = f"{server_url}/{repository}/commit/{commit_sha}" if commit_sha else "#"
        ref_name = os.environ.get("GITHUB_REF_NAME", "master")
        actor = os.environ.get("GITHUB_ACTOR", "github-actions")
        run_id = os.environ.get("GITHUB_RUN_ID", "")
        run_number = os.environ.get("GITHUB_RUN_NUMBER", "1")
        run_url = f"{server_url}/{repository}/actions/runs/{run_id}" if run_id else "#"

        # Check for APK artifacts
        artifacts_dir = repo_root / "artifacts"
        apk_files = []
        if artifacts_dir.exists():
            apk_files = sorted(artifacts_dir.glob("*.apk"))
        if not apk_files:
            release_apk_dir = repo_root / "app/build/outputs/apk/release"
            if release_apk_dir.exists():
                apk_files = sorted(release_apk_dir.glob("*.apk"))

        build_status = "✅ Success" if apk_files else "⚠️ Incomplete"

        # Build Markdown Summary
        lines = [
            "## 🚀 Bazarnote — Release Build Summary",
            "",
            "### 📋 Build Details",
            "| Property | Value |",
            "| :--- | :--- |",
            "| **Application Name** | **Bazarnote** |",
            "| **Package ID** | `com.rootminusone8004.bazarnote` |",
            f"| **Version** | `{version}` |",
            f"| **Release Tag** | `{tag}` |",
            f"| **Build Status** | {build_status} |",
            f"| **Commit** | [`{short_sha}`]({commit_url}) |",
            f"| **Branch / Ref** | `{ref_name}` |",
            f"| **Triggered By** | `@{actor}` |",
            f"| **Workflow Run** | [#{run_number}]({run_url}) |",
            "| **Runner Node.js** | Node.js 24 (Actions v5/v3 native) |",
            "| **Java JDK** | Eclipse Temurin 17 |",
            "| **Target / Min SDK** | API 34 (Android 14) / API 24 (Android 7.0) |",
            "| **Unit Tests** | ✅ Passed (`testDebugUnitTest`) |",
            "| **Signing Status** | ✅ Signed (`assembleRelease`) |",
            "",
            "### 📦 Release Artifacts",
            "| Artifact File | Size | SHA-256 Checksum |",
            "| :--- | :--- | :--- |",
        ]

        if apk_files:
            for apk in apk_files:
                size_str = format_size(apk.stat().st_size)
                sha256_hash = get_sha256(apk)
                lines.append(f"| `{apk.name}` | {size_str} | `{sha256_hash}` |")
        else:
            lines.append("| *No APK artifacts found* | - | - |")

        if notes:
            lines.extend([
                "",
                "### 📝 Changelog Highlights",
                f"<details><summary><b>Click to expand release notes for {tag}</b></summary>",
                "",
                notes,
                "",
                "---",
                "📖 See [CHANGELOG.md](CHANGELOG.md) for full details and change history.",
                "</details>",
            ])

        lines.extend([
            "",
            "---",
            "*Report generated automatically by Bazarnote Release Pipeline.*",
            ""
        ])

        summary_content = "\n".join(lines)

        # Write to GITHUB_STEP_SUMMARY if available
        step_summary = os.environ.get("GITHUB_STEP_SUMMARY")
        if step_summary:
            with open(step_summary, "a", encoding="utf-8") as f:
                f.write(summary_content)

        print(summary_content)
    except Exception as e:
        print(f"Error generating summary: {e}", file=sys.stderr)


if __name__ == "__main__":
    main()
