package IIS.wis2_backend.Models;

import java.sql.Date;

import IIS.wis2_backend.Models.Lesson.Lesson;
import IIS.wis2_backend.Models.Term.Term;
import jakarta.persistence.*;

/**
 * Model representing one schedule item (term or lesson).
 */
@Entity
@Table(name = "SCHEDULE_ITEMS")
public class ScheduleItem implements Comparable<ScheduleItem> {
    /**
     * Schedule item ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The corresponding schedule.
     */
    @ManyToOne
    private Schedule schedule;

    /**
     * Schedule item term.
     */
    @OneToOne(optional = true)
    private Term term;

    /**
     * Schedule item lesson.
     */
    @OneToOne(optional = true)
    private Lesson lesson;

    /**
     * Schedule item date.
     */
    private Date date;

    /**
     * Compare schedule items by date.
     * @param other the other schedule item to compare to
     * @return comparison result
     */
    @Override
    public int compareTo(ScheduleItem other) {
        return this.date.compareTo(other.date);
    }

    protected ScheduleItem() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
}