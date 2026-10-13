package com.learn.boot.data.student;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.learn.boot.data.common.NotFoundException;
import com.learn.boot.data.course.Course;
import com.learn.boot.data.course.CourseRepository;

@Service
@Transactional(readOnly = true)
public class StudentService {
    private final StudentRepository students;
    private final CourseRepository courses;

    public StudentService(StudentRepository students, CourseRepository courses) {
        this.students = students;
        this.courses = courses;
    }

    @Transactional
    public StudentProfile register(String name, String passportNumber) {
        Student student = students.save(new Student(name, new Passport(passportNumber)));   // passport cascades
        return StudentProfile.from(student);
    }

    @Transactional
    public StudentProfile enroll(long studentId, long courseId) {
        Student student = students.findDetailedById(studentId).orElseThrow(() -> new NotFoundException("Student", studentId));
        Course course = courses.findById(courseId).orElseThrow(() -> new NotFoundException("Course", courseId));
        student.enroll(course);
        return StudentProfile.from(student);
    }

    public StudentProfile profile(long id) {
        return StudentProfile.from(students.findDetailedById(id).orElseThrow(() -> new NotFoundException("Student", id)));
    }

    public List<String> studentsIn(String courseName) {
        return students.findByCoursesNameOrderByName(courseName).stream().map(Student::getName).toList();
    }

    public record StudentProfile(Long id, String name, String passportNumber, List<String> courses) {
        static StudentProfile from(Student s) {
            return new StudentProfile(s.getId(), s.getName(), s.getPassport().getNumber(),
                    s.getCourses().stream().map(Course::getName).sorted().toList());
        }
    }
}
