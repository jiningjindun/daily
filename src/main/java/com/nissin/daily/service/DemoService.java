package com.nissin.daily.service;

public class DemoService {

   /* *//*添加 employee*//*
    @RequestMapping("/add")
    @ResponseBody
    public String add() {
        Employee employee = new Employee("jack", 11000.0, 22);
        employeeMapper.insert(employee);
        return "ok";

    }

    *//*批量插入 employee*//*
    @RequestMapping("/bachAdd")
    @ResponseBody
    public String bachAdd() {

        Employee employee = new Employee("jack", 11000.0, 22);
        List<Employee> list = new ArrayList<>();
        Employee e = new Employee();
    *//*插入五条*//*
        for (int i = 0; i < 5; i++) {
            e.setName("cici");
            e.setSalary(13000.0);
            e.setAge(i + 20);
            list.add(e);
        }
        employeeMapper.insertList(list);
        return "ok";

    }


    *//*更据唯一主键查询*//*
    @RequestMapping("/getById")
    @ResponseBody
    public Employee getById() {
        //更据唯一主键 或者唯一编号的查询只返回一个结果
        Employee employee = new Employee();
        employee.setId(1);


        employee = employeeMapper.selectOne(employee);
        return employee;

    }


    *//*更据name查询*//*
    @RequestMapping("/getByName")
    @ResponseBody
    public List<Employee> selectByName() {
        //普通条件查询可含有多条件   查询name=cc age=100的employee
        Employee employee = new Employee();
        employee.setName("cici");
        employee.setAge(100);
        List<Employee> list = employeeMapper.select(employee);

        return list;

    }


    *//*更新 employee*//*
    @RequestMapping("/update")
    @ResponseBody
    public String update() {
        //Selective 如果有这个值的话就更新  否则不更新

        Employee employee = new Employee(1, "mm", 1.0, 1);

        //根据主键更新
        int i = employeeMapper.updateByPrimaryKeySelective(employee);
        return i + "";


    }


    *//*QBC查询*//*
    @RequestMapping("/queryByExample1")
    @ResponseBody
    public List<Employee> queryByExample1(){

        Example example=new Example(Employee.class);
        Example.Criteria criteria=example.createCriteria();
        //名字中含有c的员工  传入实体类的属性值
        criteria.andLike("name","%c%");
        //传入criteria
        return employeeMapper.selectByExample(example);

    }


    *//*QBC查询   传入两个criterial*//*
    @RequestMapping("/queryByExample2")
    @ResponseBody
    public List<Employee> queryByExample2(){

        Example example=new Example(Employee.class);
        Example.Criteria criteria=example.createCriteria();

        Example.Criteria criteria2=example.createCriteria();
        criteria.andLike("name","%c%");
        //名字中含有c的员工  传入实体类的属性值

        criteria2.andLike("name","%m%");
        //加入第二个criterial
        example.or(criteria2);

        //传入criteria
        return employeeMapper.selectByExample(example);

    }




    *//*QBC查询  去重*//*
    @RequestMapping("/queryByExample3")
    @ResponseBody
    public List<Employee> queryByExample3(){

        Example example=new Example(Employee.class);
        Example.Criteria criteria=example.createCriteria();
        //id 大于4的员工
        criteria.andGreaterThan("id","4");
        //排序
        example.orderBy("name").asc();
        //去重
        example.setDistinct(true);
        //select name,age from employee
        example.selectProperties("id","name","age");
        //传入criteria
        return employeeMapper.selectByExample(example);

    }*/


}
