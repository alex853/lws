package net.simforge.scenery.core.persistence;

import net.simforge.commons.hibernate.Auditable;
import net.simforge.commons.hibernate.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "is_revision")
public class SceneryRevision implements BaseEntity, Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "is_revision_id")
    @SequenceGenerator(name = "is_revision_id", sequenceName = "is_revision_id_seq", allocationSize = 1)
    private Integer id;
    @Version
    private Integer version;

    @SuppressWarnings("unused")
    @Column(name = "create_dt")
    private LocalDateTime createDt;
    @SuppressWarnings("unused")
    @Column(name = "modify_dt")
    private LocalDateTime modifyDt;

    @ManyToOne
    @JoinColumn(name = "scenery_id")
    private Scenery scenery;
    @Column(name = "rev_number")
    private Integer revNumber;
    private Integer status;
    private String comment;
    private String images;
    @Column(name = "repo_path")
    private String repoPath;
    @Column(name = "repo_mode")
    private Integer repoMode;
    @Column(name = "dest_path")
    private String destPath;
    @Column(name = "installation_steps")
    private String installationSteps;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getVersion() {
        return version;
    }

    @Override
    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public LocalDateTime getCreateDt() {
        return createDt;
    }

    @Override
    public LocalDateTime getModifyDt() {
        return modifyDt;
    }

    public Scenery getScenery() {
        return scenery;
    }

    public void setScenery(Scenery scenery) {
        this.scenery = scenery;
    }

    public Integer getRevNumber() {
        return revNumber;
    }

    public void setRevNumber(Integer revNumber) {
        this.revNumber = revNumber;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getRepoPath() {
        return repoPath;
    }

    public void setRepoPath(String repoPath) {
        this.repoPath = repoPath;
    }

    public Integer getRepoMode() {
        return repoMode;
    }

    public void setRepoMode(Integer repoMode) {
        this.repoMode = repoMode;
    }

    public String getDestPath() {
        return destPath;
    }

    public void setDestPath(String destPath) {
        this.destPath = destPath;
    }

    public String getInstallationSteps() {
        return installationSteps;
    }

    public void setInstallationSteps(String installationSteps) {
        this.installationSteps = installationSteps;
    }

    public static class Status {
        public static final int InProgress = 0;
        public static final int Published = 100;
    }

    public static class RepoMode {
        public static final int Package = 0;
        public static final int Archives = 1;
    }
}
