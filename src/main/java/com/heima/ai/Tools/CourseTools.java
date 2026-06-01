package com.heima.ai.Tools;

import com.heima.ai.entity.po.Course;
import com.heima.ai.entity.po.CourseReservation;
import com.heima.ai.entity.po.School;
import com.heima.ai.entity.query.CourseQuery;
import com.heima.ai.service.ICourseReservationService;
import com.heima.ai.service.ICourseService;
import com.heima.ai.service.ISchoolService;
import lombok.RequiredArgsConstructor;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.sql.Wrapper;
import java.util.List;

/**
 * ClassName:CourseTools
 * Description:
 *
 * @Author 何永琪
 * @Create 2026/5/29 16:03
 * @Version 1.0
 */
@RequiredArgsConstructor
@Component
public class CourseTools {

    private final ICourseService courseService;
    private final ISchoolService schoolService;
    private final ICourseReservationService  courseReservationService;


    @Tool(description = "根据条件查询课程信息")
    public List<Course> queryCourse(@ToolParam(description = "课程查询条件") CourseQuery courseQuery) {
        if (courseQuery == null){
            //没有查询条件的时候默认用mp中打的,list()方法操作查询数据库中所有的课程
            return courseService.list();
        }


        courseService.query().eq(courseQuery.getType() != null, "type", courseQuery.getType())
                .le(courseQuery.getEdu() != null, "edu", courseQuery.getEdu())
        ;
        //如果排序条件传过来不为null也不为空
        if (courseQuery.getSorts() != null && !courseQuery.getSorts().isEmpty()){
            //因为排序方式传过来可能有多个，所以需要循环
            for (CourseQuery.Sort sort : courseQuery.getSorts()){
                courseService.query().orderBy(true, sort.getAsc(), sort.getField());
            }
        }
        return courseService.list();
    }
    @Tool(description = "查询所有学校信息")
    public List<School> querySchool() {
            return schoolService.list();
    }
@Tool(description = "创建课程预约单")
    public Integer createCourseReservation(@ToolParam(description = "预约课程") String course,
                                           @ToolParam(description = "预约校区") String school,
                                           @ToolParam(description = "预约人姓名") String studentName,
                                           @ToolParam(description = "预约人联系方式") String contactInfo,
                                           @ToolParam(description = "预约备注",required = false) String remark) {
        CourseReservation courseReservation = new CourseReservation();
        courseReservation.setCourse(course);
        courseReservation.setSchool(school);
        courseReservation.setStudentName(studentName);
        courseReservation.setContactInfo(contactInfo);
        courseReservation.setRemark(remark);
        courseReservationService.save(courseReservation);
        //返回预约单号
        return courseReservation.getId();
    }
}
