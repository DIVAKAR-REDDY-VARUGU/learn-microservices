import { Injectable, NotFoundException } from '@nestjs/common';
import { TaskCreationDto } from './dto/create-task.dto';
import { TaskUpdateDto } from './dto/update-task.dto';

@Injectable()
export class TasksService {
  private tasks = [
    {
      id: 1,
      title: 'Learn NestJS',
      done: false,
    },
    {
      id: 2,
      title: 'Learn Spring Boot',
      done: false,
    },
  ];

  private nextId: number = 3;

  findAll() {
    return this.tasks;
  }

  findById(id: number) {
    const task = this.tasks.find((item) => item.id == id);
    if (!task) throw new NotFoundException(`Task ${id}, not found`);
    return task;
  }

  create({ title }: TaskCreationDto) {
    this.tasks.push({
      id: this.nextId++,
      title,
      done: false,
    });
  }

  update(id: number, dto: TaskUpdateDto) {
    const task = this.findById(id); // ♻️ reuse findById → it throws 404 if missing
    if (dto.title !== undefined) task.title = dto.title;
    if (dto.done !== undefined) task.done = dto.done;
    return task;
  }

  remove(id: number) {
    const index = this.tasks.findIndex((t) => t.id === id);
    if (index === -1) throw new NotFoundException(`Task ${id} not found`);
    const [removed] = this.tasks.splice(index, 1);
    return removed;
  }
}
