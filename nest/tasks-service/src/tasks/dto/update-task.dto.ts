import { IsBoolean, IsOptional, IsString } from "class-validator";

export class TaskUpdateDto{
    @IsString()
    @IsOptional()
    title?:string;

    @IsBoolean()
    @IsOptional()
    done?:boolean;
}