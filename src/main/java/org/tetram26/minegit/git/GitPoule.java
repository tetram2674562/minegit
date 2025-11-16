package org.tetram26.minegit.git;

import org.bukkit.Bukkit;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.FS;
import org.tetram26.minegit.Minegit;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GitPoule {

    private String username;
    private String password;

    private String repoURL;
    private String branch;

    private final Lock lock = new ReentrantLock(true);
    public static GitPoule INSTANCE;

    private GitPoule(Minegit plugin) {
        this.username = plugin.getConfig().getString("username");
        this.password = plugin.getConfig().getString("password");

        this.repoURL = plugin.getConfig().getString("repoURL");
        this.branch = plugin.getConfig().getString("branch");
        if (repoURL != null && repoURL.isEmpty()) {
            Minegit.getInstance().getComponentLogger().error("repoURL in config.yml is empty!");
            throw new IllegalStateException("repoURL missing");
        }
        if (branch != null && branch.isEmpty()) {
            Minegit.getInstance().getComponentLogger().error("branch in config.yml is empty!");
            throw new IllegalStateException("branch missing");
        }
    }

    public static void init() {
        INSTANCE = new GitPoule(Minegit.getInstance());
    }

    public Lock getLock() {
        return lock;
    }

    public void pull() throws GitAPIException, IOException, URISyntaxException {
        lock.lock();
        try {
            CredentialsProvider creds =
                    new UsernamePasswordCredentialsProvider(username, password);

            File workTree = new File(".");
            File gitDir = new File(".git");

            boolean firstTime = !RepositoryCache.FileKey.isGitRepository(gitDir, FS.DETECTED);
            if (firstTime) {
                Minegit.getInstance().getComponentLogger().info("Initializing Git repo in server root…");
                try (Git git = Git.init()
                        .setDirectory(workTree)
                        .call()) {
                    git.remoteAdd()
                            .setName("origin")
                            .setUri(new URIish(repoURL))
                            .call();
                    git.fetch()
                            .setCredentialsProvider(creds)
                            .call();
                    git.reset()
                            .setMode(ResetCommand.ResetType.HARD)
                            .setRef("origin/" + branch)
                            .call();
                    boolean branchExists = git.getRepository().findRef(branch) != null;

                    if (!branchExists) {
                        git.checkout()
                                .setName(branch)
                                .setStartPoint("origin/" + branch)
                                .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM)
                                .setCreateBranch(true)
                                .call();
                    } else {
                        git.checkout()
                                .setName(branch)
                                .call();
                    }
                }

                Minegit.getInstance().getComponentLogger().info("Git initialized and synchronized.");
                return;
            }
            try (Git git = Git.open(gitDir)) {
                git.fetch()
                        .setCredentialsProvider(creds)
                        .call();
                git.reset()
                        .setMode(ResetCommand.ResetType.HARD)
                        .setRef("origin/" + branch)
                        .call();
            }

        } finally {
            lock.unlock();
        }
    }

    public boolean remoteHasChanged() throws IOException, GitAPIException {
        lock.lock();
        try {
            CredentialsProvider creds =
                    new UsernamePasswordCredentialsProvider(username, password);
            File gitDir = new File(".git");
            try (Git git = Git.open(gitDir)) {
                git.fetch()
                        .setCredentialsProvider(creds)
                        .call();
                String currentBranch = git.getRepository().getBranch(); // e.g. "main"
                String remoteBranchRef = "refs/remotes/origin/" + currentBranch;
                Ref remoteRef = git.getRepository().findRef(remoteBranchRef);
                Ref localRef = git.getRepository().findRef(currentBranch);
                if (remoteRef == null || localRef == null) {
                    return false;
                }
                String localCommit = localRef.getObjectId().getName();
                String remoteCommit = remoteRef.getObjectId().getName();
                return !localCommit.equals(remoteCommit);
            }
        } finally {
            lock.unlock();
        }
    }

    public void reload(Minegit plugin) {
        lock.lock();
        this.username = plugin.getConfig().getString("username");
        this.password = plugin.getConfig().getString("password");
        this.repoURL = plugin.getConfig().getString("repoURL");
        this.branch = plugin.getConfig().getString("branch");

        if (repoURL.isEmpty()) {
            Minegit.getInstance().getComponentLogger().error("repoURL in config.yml is empty!");
            throw new IllegalStateException("repoURL missing");
        }
        if (branch != null && branch.isEmpty()) {
            Minegit.getInstance().getComponentLogger().error("branch in config.yml is empty!");
            throw new IllegalStateException("branch missing");
        }
        lock.unlock();
    }

    public Repository getRepository() throws IOException {
        return new FileRepository(new File(".git"));
    }
}
