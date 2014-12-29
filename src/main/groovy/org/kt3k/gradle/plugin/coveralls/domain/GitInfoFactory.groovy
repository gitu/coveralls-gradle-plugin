package org.kt3k.gradle.plugin.coveralls.domain

import org.eclipse.jgit.lib.*
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk

/**
 * GitInfoFactory is factory class of GitInfo.
 * This class is based on GitRepository from https://github.com/trautonen/coveralls-maven-plugin
 */
class GitInfoFactory {

    /**
     * Create GitInfo instance from directory.
     *
     */
    public static GitInfo load(File sourceDirectory) throws IOException {
        def repositoryOptional = getRepositoryOptional(sourceDirectory)
        if (repositoryOptional.isPresent()) {
            def repository = repositoryOptional.get()
            try {
                return new GitInfo(
                        head: getHead(repository),
                        branch: getBranch(repository),
                        remotes: getRemotes(repository));
            } finally {
                repository.close();
            }
        } else {
            return null
        }

    }

    private static GitInfo.Head getHead(final Repository repository) throws IOException {
        ObjectId revision = repository.resolve(Constants.HEAD);
        RevCommit commit = new RevWalk(repository).parseCommit(revision);
        GitInfo.Head head = new GitInfo.Head(
                id: revision.getName(),
                authorName: commit.getAuthorIdent().getName(),
                authorEmail: commit.getAuthorIdent().getEmailAddress(),
                committerName: commit.getCommitterIdent().getName(),
                committerEmail: commit.getCommitterIdent().getEmailAddress(),
                message: commit.getFullMessage()
        );
        return head;
    }

    private static String getBranch(final Repository repository) throws IOException {
        return repository.getBranch();
    }

    private static List<GitInfo.Remote> getRemotes(final Repository repository) {
        Config config = repository.getConfig();
        List<GitInfo.Remote> remotes = new ArrayList<GitInfo.Remote>();
        for (String remote : config.getSubsections("remote")) {
            String url = config.getString("remote", remote, "url");
            remotes.add(new GitInfo.Remote(name: remote, url: url));
        }
        return remotes;
    }

    private static Optional<Repository> getRepositoryOptional(File repoUri) {
        def repositoryBuilder = new RepositoryBuilder().findGitDir(repoUri);
        if (repositoryBuilder.getGitDir() == null && repositoryBuilder.getWorkTree() == null) {
            return Optional.empty()
        } else {
            return Optional.of(repositoryBuilder.build())
        }
    }
}
