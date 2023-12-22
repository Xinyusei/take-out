package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }


        //密码比对
        // TODO 后期需要进行md5加密，然后再进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     *
     * @param employeeDTO
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {
        System.out.println("ThreadName: " + Thread.currentThread().getName());

        Employee employee = new Employee();

        //TODO 对象属性拷贝  - 没学过
        BeanUtils.copyProperties(employeeDTO, employee);

        ////设置账号的状态，默认正常状态 1表示正常 0表示锁定
        employee.setStatus(StatusConstant.ENABLE);

        //设置密码
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));


        //TODO 设置当前记录创建人id和修改人id
        //先写个假的,以后修改
        /*
        基于JWT令牌获取
        String token = httpServletRequest.getHeader(jwtProperties.getAdminTokenName());
        Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
        Long empId = Long.valueOf(claims.get(JwtClaimsConstant.EMP_ID).toString());
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);
        */


        //设置当前记录创建人id和修改人id
        /*
        //TODO 已经在AOP中实现
         //设置createTime
        employee.setCreateTime(LocalDateTime.now());
        //设置更新时间
        employee.setCreateTime(LocalDateTime.now());
        Long currentId = BaseContext.getCurrentId();
        employee.setCreateUser(currentId);
        employee.setUpdateUser(currentId);

        */
        //调用持久层方法
        employeeMapper.insert(employee);


    }

    /**
     * 员工条件分页查询
     *
     * @param employeePageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        /*
        老版操作
        int pageSize = employeePageQueryDTO.getPageSize();
        int startPage = (employeePageQueryDTO.getPage() - 1) * pageSize;
        String name = employeePageQueryDTO.getName();

        List<Employee> records = employeeMapper.page(name, startPage, pageSize);
        Long total = employeeMapper.pageTotal(name);
        PageResult pageResult = new PageResult(total, records);
        */
        //1.设置分页参数
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        //2.执行查询
        List<Employee> employeeList = employeeMapper.page(employeePageQueryDTO.getName());
        //3.强转
        Page list = (Page) employeeList;
        PageResult pageResult = new PageResult(list.getTotal(), list.getResult());
        return pageResult;
    }


    /**
     * 启用或者禁用员工账号
     *
     * @param status 代表员工现在的状态
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        //status - 0 代表禁用,1代表启用

        //提高复用性
        //TODO 这里为了扩展以后的功能,最好传对象 写动态SQL

        //TODO 这个是链式XXX 不太懂
        Employee employee = Employee.builder()
                .status(status)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .id(id)
                .build();

        employeeMapper.update(employee);
    }

    /**
     * 根据id获取员工信息
     *
     * @param id
     * @return
     */
    @Override
    public Employee getById(Long id) {
        Employee employee = employeeMapper.getbyId(id);
        return employee;
    }

    /**
     * 修改员工信息
     *
     * @param employeeDTO
     */
    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = Employee.builder()
                //TODO 已经通过AOP实现了
                //.updateTime(LocalDateTime.now())
                //.updateUser(BaseContext.getCurrentId())
                .build();
        BeanUtils.copyProperties(employeeDTO, employee);
        employeeMapper.update(employee);
    }
}
