package IIS.wis2_backend.Services.Education;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import IIS.wis2_backend.DTO.Request.Term.TermCreationDTO;
import IIS.wis2_backend.DTO.Response.Term.LightweightTermDTO;
import IIS.wis2_backend.Exceptions.ExceptionTypes.NotFoundException;
import IIS.wis2_backend.Exceptions.ExceptionTypes.NotImplementedException;
import IIS.wis2_backend.Models.Room.Room;
import IIS.wis2_backend.Models.Term.MidtermExam;
import IIS.wis2_backend.Models.Term.Term;
import IIS.wis2_backend.Models.User.Teacher;
import IIS.wis2_backend.Repositories.RoomRepository;
import IIS.wis2_backend.Repositories.Education.Term.ExamRepository;
import IIS.wis2_backend.Repositories.Education.Term.MidtermExamRepository;
import IIS.wis2_backend.Repositories.Education.Term.TermRepository;
import IIS.wis2_backend.Repositories.User.TeacherRepository;

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
     * To fetch teachers.
     */
    private final TeacherRepository teacherRepository;

    /**
     * To fetch rooms.
     */
    private final RoomRepository roomRepository;

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
     * @param scheduleService        the schedule service
     * @param teacherRepository      the teacher repository
     * @param roomRepository         the room repository
     */
    public TermService(TermRepository termRepository, ExamRepository examRepository,
            MidtermExamRepository midtermExamRepository, ScheduleService scheduleService,
            TeacherRepository teacherRepository, RoomRepository roomRepository) {
        this.termRepository = termRepository;
        this.examRepository = examRepository;
        this.midtermExamRepository = midtermExamRepository;
        this.scheduleService = scheduleService;
        this.teacherRepository = teacherRepository;
        this.roomRepository = roomRepository;
    }

    /**
     * Creates a midterm exam based on the provided DTO.
     * 
     * @param dto the term creation DTO
     * @return the created lightweight term DTO
     */
    public LightweightTermDTO CreateMidtermExam(TermCreationDTO dto) {
        // Get needed entities
        Teacher supervisor = GetSupervisor(dto.getSupervisorID());
        Set<Room> rooms = GetRooms(dto.getRoomIDs());

        // Create midterm exam
        MidtermExam midtermExam = MidtermExam.builder()
                .minPoints(dto.getMinPoints())
                .maxPoints(dto.getMaxPoints())
                .date(dto.getDate())
                .duration(dto.getDuration())
                .description(dto.getDescription())
                .name(dto.getName())
                .mandatory(dto.getMandatory())
                .supervisor(supervisor)
                .rooms(rooms)
                .build();

        midtermExamRepository.save(midtermExam);

        if (dto.getAutoRegistration()) {
            Autoregister(midtermExam, Optional.empty());
        }

        scheduleService.CreateScheduleForTerm(midtermExam, "midterm");
        return ConvertToLightweightDTO(midtermExam);
    }

    /**
     * Basically calls GetTeacherByID or throws.
     * 
     * @param supervisorID the ID of the supervisor
     * @return the teacher
     */
    private Teacher GetSupervisor(Long supervisorID) {
        return teacherRepository.findById(supervisorID)
                .orElseThrow(() -> new NotFoundException("Teacher not found with ID: " + supervisorID));
    }

    /**
     * Fetches rooms based on provided IDs.
     * 
     * @param roomIDs the set of room IDs
     * @return the set of rooms
     */
    private Set<Room> GetRooms(Set<Long> roomIDs) {
        return roomRepository.findAllById(roomIDs)
                .stream()
                .collect(Collectors.toSet());
    }

    /**
     * Auto-registers students for the term.
     * 
     * @param term        the term
     * @param whichAttempt optional attempt number
     */
    private void Autoregister(Term term, Optional<Integer> whichAttempt) {
        throw new NotImplementedException("Auto-registration not implemented yet.");
    }

    /**
     * Converts a Term entity to a LightweightTermDTO.
     * 
     * @param term the term entity
     * @return the lightweight term DTO
     */
    private LightweightTermDTO ConvertToLightweightDTO(Term term) {
        return new LightweightTermDTO(
                term.getId(),
                term.getName(),
                term.getDate(),
                term.getDuration());
    }
}