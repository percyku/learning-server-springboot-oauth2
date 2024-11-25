package com.percyku.learningserver.learningserverspringboot.controller;

import com.percyku.learningserver.learningserverspringboot.dto.CourseRequest;
import com.percyku.learningserver.learningserverspringboot.service.CourseService;
import com.percyku.learningserver.learningserverspringboot.service.UserService;
import com.percyku.learningserver.learningserverspringboot.util.PageCourse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Validated
@RestController
public class CourseController {
    private final static Logger log = LoggerFactory.getLogger(GeneralExceptionHandler.class);


    @Autowired
    CourseService courseService;
    @Autowired
    UserService userService;

    @GetMapping("/api/courses/student/{id}")
    public ResponseEntity<List<PageCourse>> getEnrolledCourses(Authentication authentication,
                                                     @PathVariable Long id){

        List<PageCourse> courses = courseService.getCourseByStudent(id);

        for(PageCourse course : courses) {
            log.debug(course.toString());
        }
        return  ResponseEntity.status(HttpStatus.OK).body(courses);

    }

    @GetMapping("/api/courses/instructor/{id}")
    public ResponseEntity<List<PageCourse>> getCoursesByInstructor(Authentication authentication,
                                                               @PathVariable Long id){

        List<PageCourse> courses = courseService.getCourseByInstructor(id);

        return  ResponseEntity.status(HttpStatus.OK).body(courses);
    }

    @GetMapping("/api/courses/findByName")
    public ResponseEntity<List<PageCourse>>getCourseByName(Authentication authentication,
                                                           @RequestParam(value="courseName", required=true) String courseName){
//        List<PageCourse> courses = courseService.getCourseByCourseName(authentication.getName(),courseName);
        List<PageCourse> courses = courseService.getCourseByCourseName(getName(authentication),courseName);

        return  ResponseEntity.status(HttpStatus.OK).body(courses);

    }

    @PostMapping("/api/courses")
    public ResponseEntity<PageCourse> createCourse(Authentication authentication,@RequestBody @Valid CourseRequest courseRequest){


        PageCourse pageCourse =courseService.createCourse(getName(authentication),courseRequest);
//        PageCourse pageCourse =courseService.createCourse(authentication.getName(),courseRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(pageCourse);
//        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PutMapping("/api/courses/enroll/{courseId}")
    public ResponseEntity<PageCourse> enrollCourse(Authentication authentication,
                                                                   @PathVariable int courseId) {

        PageCourse pageCourse =courseService.enrollCourse( getName(authentication),courseId);
//        PageCourse pageCourse =courseService.enrollCourse(authentication.getName(),courseId);
        if(pageCourse == null){
            throw new CommonException("This course wasn't exist,please check this course id:"+courseId);
        }
        return ResponseEntity.status(HttpStatus.OK).body(pageCourse);
    }


    private String getName(Authentication authentication){
        String res ="";
        ///^(\w)+(\.\w+)*@(\w)+((\.\w+)+)$/;
        //^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
        //if(authentication.getName().matches("/^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)$/;")){
        String regex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";
        Pattern pattern =Pattern.compile(regex);
        if(pattern.matcher(authentication.getName()).find()){
            res= authentication.getName();
        }else{
            DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes =principal.getAttributes();
            res =attributes.get("email").toString();

        }


        return res;
    }

}
