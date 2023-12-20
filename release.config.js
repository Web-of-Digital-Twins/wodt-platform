// Load this configuration that provide all the base for working with conventional commits
const config = require('semantic-release-preconfigured-conventional-commits')

/*
 Commands executed during release.
 It also set an environment variable "release" indicating that the release was successful.
*/
const publishCommands = `
git tag -a -f \${nextRelease.version} \${nextRelease.version} -F CHANGELOG.md || exit 2
git push --force origin \${nextRelease.version} || exit 3
echo "release_status=released" >> $GITHUB_ENV
echo "CONTAINER_VERSION="\${nextRelease.version} >> $GITHUB_ENV
`
// Only release on branch main
const releaseBranches = ["main"]

config.branches = releaseBranches

config.plugins.push(
    // Custom release commands
    ["@semantic-release/exec", {
        "publishCmd": publishCommands,
    }],
    // Release also in GitHub
    ["@semantic-release/github", {
        "assets": []
    }],
    ["@semantic-release/git", {
        "assets": ["CHANGELOG.md", "package.json"],
        "message": "chore(release)!: [skip ci] ${nextRelease.version} released"
    }]
)

// JS Semantic Release configuration must export the JS configuration object
module.exports = config
