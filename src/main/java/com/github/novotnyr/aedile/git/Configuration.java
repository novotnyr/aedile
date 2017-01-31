package com.github.novotnyr.aedile.git;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

/**
 * Represent deserialized configuration of synchronization process.
 * <p>
 *     This is corresponding to the {@code git2consul} configuration.
 * </p>
 * @see <a href="https://github.com/Cimpress-MCP/git2consul">documentation of git2consul</a>
 */
public class Configuration {
    private String localStore;

    private List<Repo> repos = new LinkedList<>();

    public List<Repo> getRepos() {
        return repos;
    }

    public void setRepos(List<Repo> repos) {
        this.repos = repos;
    }

    public String getLocalStore() {
        return localStore;
    }

    /**
     * Set the directory in the local filesystem that will contain
     * cloned Git repo.
     * @param localStore local directory in the filesystem.
     */
    public void setLocalStore(String localStore) {
        this.localStore = localStore;
    }

    public class Repo {
        private String url;

        private List<String> branches = new LinkedList<>();

        private String sourceRoot;

        @SerializedName("mountpoint")
        private String mountPoint;

        public String getUrl() {
            return url;
        }

        /**
         * Set the Git URL of the repository.
         * <p>
         *     Example: <code>https://github.com/ryanbreen/git2consul_data.git</code>
         * </p>
         * @param url Git URL of the repository
         */
        public void setUrl(String url) {
            this.url = url;
        }

        public List<String> getBranches() {
            return branches;
        }

        public void setBranches(List<String> branches) {
            this.branches = branches;
        }

        public String getSourceRoot() {
            return sourceRoot;
        }

        /**
         * Set subdirectory in the Git repo that is navigated before mapping files to KVs.
         * If the repo has structure <code>apps/xenon/web/config.json</code>
         * and the source root si <code>apps/xenon</code>, the file will
         * be put into Consul K/V as a <code>/web/config.json</code>.
         */
        public void setSourceRoot(String sourceRoot) {
            this.sourceRoot = sourceRoot;
        }

        public String getMountPoint() {
            return mountPoint;
        }

        /**
         * Prefix of keys in the Consul K/V. See
         * <tt>git2consul</tt> and the documentation
         * for <tt>mountpoint</tt>.
         */
        public void setMountPoint(String mountPoint) {
            this.mountPoint = mountPoint;
        }
    }
}
