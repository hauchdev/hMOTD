# Release process

This document describes how a new release of hMOTD is created and published.

## Automation overview

| Workflow | Trigger | Purpose |
| --- | --- | --- |
| `ci.yml` | push / pull request on `master` | Builds, tests and verifies the release profile |
| `release.yml` | push of a `v*` tag | Builds, signs and publishes the GitHub Release |
| `modrinth.yml` | published GitHub Release | Publishes the plugin to Modrinth |

## 1. Prepare the release

1. Update `CHANGELOG.md` with the changes of the new version.
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

Create and push the tag, the tag format is `v<version>`:

```bash
git tag -a v1.2.0 -m "Release 1.2.0"
git push origin v1.2.0
```

Pushing the tag triggers `release.yml`, which:

1. builds the project with the `release` profile (`-Dgpg.skip=true`),
2. imports the GPG key and signs the artifacts,
3. collects the JARs and generates a `checksums.txt`,
4. publishes everything as a GitHub Release.

## 3. Maven Central (optional)

The `publish-central` job of `release.yml` only runs when the following repository secrets
are configured:

| Secret | Description |
| --- | --- |
| `GPG_PRIVATE_KEY` | The ASCII armored private GPG key used to sign the artifacts |
| `MAVEN_GPG_PASSPHRASE` | The passphrase of the GPG key |
| `MAVEN_CENTRAL_USERNAME` | The Sonatype account user name |
| `MAVEN_CENTRAL_PASSWORD` | The Sonatype account token |

Artifacts are uploaded to the Sonatype staging repository. The staging repository has to be
closed and released manually in the [Sonatype Central Portal](https://central.sonatype.com/).

## 4. Modrinth (optional)

`modrinth.yml` publishes the plugin to Modrinth once the GitHub Release exists. It requires:

| Secret / variable | Description |
| --- | --- |
| `MODRINTH_TOKEN` | The Modrinth API token (secret) |
| `MODRINTH_PROJECT_ID` | The Modrinth project id (variable) |

## Secrets checklist

- [ ] `GPG_PRIVATE_KEY`
- [ ] `MAVEN_GPG_PASSPHRASE`
- [ ] `MAVEN_CENTRAL_USERNAME`
- [ ] `MAVEN_CENTRAL_PASSWORD`
- [ ] `MODRINTH_TOKEN`
- `MODRINTH_PROJECT_ID` (repository variable)

## Manual release

The release can also be triggered manually from the **Actions** tab using the *Release* workflow.
The `tag` input defines which tag is released, the tag itself is created by the workflow when it
does not exist yet.

## Local verification

To verify that the release artifacts can be built without publishing anything:

```bash
mvn -Prelease clean package -Dgpg.skip=true
```

This produces:

* `target/hMOTD-<version>.jar` — the plugin
* `target/hMOTD-<version>-sources.jar` — the sources
* `target/hMOTD-<version>-javadoc.jar` — the documentation
