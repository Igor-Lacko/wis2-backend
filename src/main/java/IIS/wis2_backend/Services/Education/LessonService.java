package IIS.wis2_backend.Services.Education;

import org.springframework.stereotype.Service;

import IIS.wis2_backend.Repositories.Education.Lesson.LabRepository;
import IIS.wis2_backend.Repositories.Education.Lesson.LectureRepository;
import IIS.wis2_backend.Repositories.Education.Lesson.LessonRepository;

/**
 * Service for managing lessons.
 */
@Service
public class LessonService {
    /**
     * Lesson repository for RUD operations.
     */
    private final LessonRepository lessonRepository;

    /**
     * Lecture repository as one of the concrete lesson types for C operations.
     */
    private final LectureRepository lectureRepository;

    /**
     * And lab repository!
     */
    private final LabRepository labRepository;

    /**
     * Constructor for LessonService.
     * 
     * @param lessonRepository  Repository for lessons.
     * @param lectureRepository Repository for lectures.
     * @param labRepository     Repository for labs.
     */
    public LessonService(LessonRepository lessonRepository, LectureRepository lectureRepository, LabRepository labRepository) {
        this.lessonRepository = lessonRepository;
        this.lectureRepository = lectureRepository;
        this.labRepository = labRepository;
    }
}