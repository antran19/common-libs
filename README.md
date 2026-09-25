# common-libs

Shared Java libraries for Project Nexus's microservices (`user-service`, `catalog-service`,
and future services). Extracted from the original monorepo so each service can be its own
independent repository/project, per the team's polyrepo requirement.

Modules: `common-core`, `common-web`, `common-events`, `common-security`.

## Build

```bash
mvn clean install
```

This installs the 4 artifacts (version `1.0.0`) into your local `~/.m2/repository`, where
any other Maven project on the same machine — including `user-service` and
`catalog-service` — can resolve them as normal versioned dependencies. That local install
is enough to build on your own machine; you don't need GitHub Packages access just to
develop here.

## Publishing (GitHub Packages)

`git push` to `main` triggers `.github/workflows/publish.yml`, which runs `mvn deploy` and
publishes all 4 artifacts to this repo's GitHub Packages Maven registry
(`https://maven.pkg.github.com/antran19/common-libs`). No extra secret is needed — it uses
the workflow's built-in `GITHUB_TOKEN`.

Bump the `<version>` in the root `pom.xml` (and the 4 modules stay in lockstep with it via
the parent) whenever shared code changes, then push — CI publishes the new version
automatically. Update the `common-libs.version` property in `user-service`'s and
`catalog-service`'s `pom.xml` to match once you want them to pick it up.

## One-time setup for teammates and other CI (reading the published packages)

GitHub Packages' Maven registry requires authentication to **read** packages, even from a
public repository — unlike Docker/npm packages on GitHub, there's no truly anonymous pull.
Anyone building `user-service` or `catalog-service` without a local `mvn install` of this
repo first needs a GitHub Personal Access Token with the `read:packages` scope, added to
their own `~/.m2/settings.xml`:

```xml
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>YOUR_PERSONAL_ACCESS_TOKEN</password>
    </server>
  </servers>
</settings>
```

(Generate a token at GitHub → Settings → Developer settings → Personal access tokens, with
at least `read:packages`.) A downstream repo's own CI workflow needs the equivalent — set
`server-id: github` in its `actions/setup-java` step, same as this repo's `publish.yml`
does; `permissions: packages: read` plus the built-in `GITHUB_TOKEN` is enough there, no
personal token required in CI.
