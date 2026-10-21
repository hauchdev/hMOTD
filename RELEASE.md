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
2. imports the GPG key (when the secret is configured) and signs the artifacts,
3. builds and tests the project with the `release` profile,
4. verifies that the plugin descriptor inside the JAR contains the correct version and main class,
5. collects the JARs and generates a `checksums.txt` with the SHA-256 sums,
6. builds the release notes from the `CHANGELOG.md` section of the released version,
7. publishes everything as a GitHub Release.

The release is marked as a pre-release automatically when the tag contains a dash, for example
`v1.2.0-beta1`.

## 3. Manual release

The workflow can also be triggered manually from the **Actions** tab:

* `tag` — the tag to release, for example `v1.2.0`. The tag is created when it does not exist yet.
* `draft` — set to `true` to create the release as a draft, so it can be reviewed before it becomes
  public.

## 4. Secrets

Only one secret is needed, and it is optional:

| Secret | Description |
| --- | --- |
| `GPG_PRIVATE_KEY` | The ASCII armored private GPG key used to sign the artifacts |
| `MAVEN_GPG_PASSPHRASE` | The passphrase of the GPG key |

When `GPG_PRIVATE_KEY` is not configured the release is still published, the artifacts are just not
signed. The detached `.asc` signatures are attached to the release as well.

## 5. Release assets

| File | Description |
| --- | --- |
| `hMOTD-<version>.jar` | The plugin, this is the file that has to be copied into `plugins/` |
| `hMOTD-<version>-sources.jar` | Sources |
| `hMOTD-<version>-javadoc.jar` | API documentation |
| `*.jar.asc` | GPG signatures, only present when the GPG key is configured |
| `checksums.txt` | SHA-256 sums of the JARs |

## Local verification

To verify that the release artifacts can be built without publishing anything:

```bash
mvn -Prelease clean package -Dgpg.skip=true
```

This produces:

* `target/hMOTD-<version>.jar` — the plugin
* `target/hMOTD-<version>-sources.jar` — the sources
* `target/hMOTD-<version>-javadoc.jar` — the documentation
