package com.sky.mapper;

import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     *
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    @Insert("insert into employee(name, username, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user) " +
            "VALUES (#{name},#{username},#{password},#{phone},#{sex},#{idNumber},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void insert(Employee employee);


    /*
    @Select("select * from employee where name = #{name} limit #{startPage}, #{pageSize}")
    List<Employee> page(String name, int startPage, int pageSize);

    @Select("select count(*) from employee where name = #{name}")
    Long pageTotal(String name);
    */


    List<Employee> page(String name);


    /**
     * 更新员工状态
     * @param employee
     */
    void update(Employee employee);

    /**
     * 根据id查询员工信息
     * @param id 员工id
     * @return
     */
    @Select("select * from employee where id = #{id};")
    Employee getbyId(Long id);
}
