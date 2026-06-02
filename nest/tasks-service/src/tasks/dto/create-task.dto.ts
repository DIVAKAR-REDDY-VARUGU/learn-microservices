import { IsNotEmpty, IsString } from "class-validator";

export class TaskCreationDto {
  @IsString()
  @IsNotEmpty()
  title!: string;
}
