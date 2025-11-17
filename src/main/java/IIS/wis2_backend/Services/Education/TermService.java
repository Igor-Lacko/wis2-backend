package IIS.wis2_backend.Services.Education;

import org.springframework.stereotype.Service;

import IIS.wis2_backend.Repositories.Education.Term.ExamRepository;
import IIS.wis2_backend.Repositories.Education.Term.MidtermExamRepository;
import IIS.wis2_backend.Repositories.Education.Term.TermRepository;

/**
 * Service for managing terms.
 */
@Service
public class TermService {
    /**
     * Term repository (generic read/delete/update).
     */
    private final TermRepository termRepository;

    /**
     * Final exam repository (create, specific stuff).
     */
    private final ExamRepository examRepository;

    /**
     * Midterm exam repository (create, specific stuff).
     */
    private final MidtermExamRepository midtermExamRepository;

    /**
     * Schedule service to update schedules of everyone affiliated with a term.
     */
    private final ScheduleService scheduleService;

    /**
     * Constructor for TermService.
     *
     * @param termRepository         the term repository
     * @param scheduleItemRepository the schedule item repository
     * @param examRepository         the final exam repository
     * @param midtermExamRepository  the midterm exam repository
     */
    public TermService(TermRepository termRepository, ExamRepository examRepository,
            MidtermExamRepository midtermExamRepository, ScheduleService scheduleService) {
        this.termRepository = termRepository;
        this.examRepository = examRepository;
        this.midtermExamRepository = midtermExamRepository;
        this.scheduleService = scheduleService;
    }
}