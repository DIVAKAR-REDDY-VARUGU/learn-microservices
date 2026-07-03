package com.learn.tasks;

// [STEP 0] App entry point — @SpringBootApplication boots the container, web server, and component scan (≈ Nest main.ts).
import com.learn.tasks.task.Task;
import com.learn.tasks.task.TaskRepository;
import com.learn.tasks.task.TaskStatus;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication // scans com.learn.tasks + sub-packages, so all @Component/@Service/@RestController are found automatically
public class TasksServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TasksServiceApplication.class, args);
    }

    // Seed a few tasks once at startup so GET returns data immediately (a CommandLineRunner runs after boot).
    @Bean
    CommandLineRunner seed(TaskRepository repo) {
        return args -> {
            if (repo.count() > 0) return;
            repo.save(newTask("Set up project", TaskStatus.DONE, 2, LocalDate.now().plusDays(1)));
            repo.save(newTask("Write REST endpoints", TaskStatus.IN_PROGRESS, 1, LocalDate.now().plusDays(3)));
            repo.save(newTask("Learn Spring Boot", TaskStatus.TODO, 3, LocalDate.now().plusDays(7)));
        };
    }

    private static Task newTask(String title, TaskStatus status, int priority, LocalDate due) {
        Task t = new Task();
        t.setTitle(title); t.setStatus(status); t.setPriority(priority); t.setDueDate(due);
        return t;
    }
}


/*

// AppModule java
@SpringBootApplication
public Class AppModule{
    public static void main(String[] arg){
        SpringApplication.run(AppModule.class,arg);
    }
}


// NestMiddleware : LoggingMiddleware
@Component
public class LoggingMiddleware extends OncePerRequestFilter{
    private static final Logger log=LoggerFactory.getLogger(LoggingMiddleware.class);
    @Override
    public void requestFilter(HttpServletRequest req,HttpServletResponse res,Object next){
        log.info("some logs")
        try{

        }catch(Exception e){
            next.doFilter(req,res);
        }finally{

        }
    }
}


// Auth Guard + Nest Interceptor 
@Configuration 
public class WebConfigurations extends  WebMvcConfigurer{
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(new CustomInterceptor()).addPatterns("/api/**");
    }
}


@Component
public class CustomInterceptor extends 


*/