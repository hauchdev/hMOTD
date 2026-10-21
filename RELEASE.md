# Release process

This document describes how a new release of hMOTD is created and published.

Everything happens on GitHub: pushing a tag triggers GitHub Actions, which builds the plugin and
publishes it as a **GitHub Release**.

## Automation overview

| Workflow | Trigger | Purpose |
| --- | --- | --- |
| `ci.yml` | push / pull request on `master` | Builds, tests and verifies the release profile |
| `release.yml` | push of a `v*` tag | Builds, signs and publishes the GitHub Release |

## 1. Prepare the release

1. Update `CHANGELOG.md` with the changes of the new version. The section header has to match the
   version exactly, for example `## 1.2.0`, because the workflow copies that section into the
   release notes.
2. Bump the version in `pom.xml`:
   ```bash
   mvn versions:set -DnewVersion=1.2.0
   ```
3. Make sure everything is committed and pushed:
   ```bash
   git add -A
   git commit -m "Release version 1.2.0"
   git push origin master
   ```

## 2. Tag and publish

The tag format is `v<version>`, the version has to match the version in `pom.xml`:

```bash
git tag -a v1.2.0 -m "Release 1.2.0"
git push origin v1.2.0
```

Pushing the tag triggers `release.yml`, which:

1. reads the project version from `pom.xml`,
2. builds and tests the project with the `release` profile,
3. verifies that the plugin descriptor inside the JAR contains the correct version and main class,
4. collects the JARs and generates a `checksums.txt` with the SHA-256 sums,
5. builds the release notes from the `CHANGELOG.md` section of the released version,
6. publishes everything as a GitHub Release.

The release is marked as a pre-release automatically when the tag contains a dash, for example
`v1.2.0-beta1`.

## 3. Manual release

The workflow can also be triggered manually from the **Actions** tab:

* `tag` — the tag to release, for example `v1.2.0`. The tag is created when it does not exist yet.
* `draft` — set to `true` to create the release as a draft, so it can be reviewed before it becomes
  public.

## 4. No secrets required

The workflow needs **no secrets at all**. The `GITHUB_TOKEN` that GitHub provides automatically is
enough to create the release, because the workflow only requests the `contents: write` permission.

## 5. Release assets

| File | Description |
| --- | --- |
| `hMOTD-<version>.jar` | The plugin, this is the file that has to be copied into `plugins/` |
| `hMOTD-<version>-sources.jar` | Sources |
| `hMOTD-<version>-javadoc.jar` | API documentation |
| `checksums.txt` | SHA-256 sums of the JARs |

## Local verification

To verify that the release artifacts can be built without publishing anything:

```bash
mvn -Prelease clean package
```

This produces:

* `target/hMOTD-<version>.jar` — the plugin
* `target/hMOTD-<version>-sources.jar` — the sources
* `target/hMOTD-<version>-javadoc.jar` — the documentation
