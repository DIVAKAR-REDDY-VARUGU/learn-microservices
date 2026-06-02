import { Body, Controller, Delete, Get, Param, ParseIntPipe, Patch, Post} from '@nestjs/common';
import { TasksService } from './tasks.service';
import { TaskCreationDto } from './dto/create-task.dto';
import { TaskUpdateDto } from './dto/update-task.dto';



@Controller('tasks')
export class TasksController {
    constructor(private readonly tasksService:TasksService){}

    @Get()
    getAllTasks(){
        return this.tasksService.findAll()
    }

    @Get(":id")
    getTaskById(
        @Param("id",ParseIntPipe) id:number
    ){
        return this.tasksService.findById(id);
    }

    @Post()
    create(
        @Body() body: TaskCreationDto
    ){
        return this.tasksService.create(body);
    }



    @Patch(":id")
    updateByPatch(
        @Param('id',ParseIntPipe) id:number,
        @Body() body:TaskUpdateDto
    ){
        return this.tasksService.update(id,body);
    }

    @Delete(":id")
    deleteById(
        @Param('id',ParseIntPipe) id:number
    ){
        return this.tasksService.remove(id);
    }

}
