from fastapi import FastAPI, Depends, HTTPException
from pydantic import BaseModel, ConfigDict
from sqlalchemy.orm import Session
from database import engine, get_db, Base
import models

Base.metadata.create_all(bind=engine)            # create tables on startup

app = FastAPI()


class TaskIn(BaseModel):                          # request DTO
    title: str
    is_done: bool = False


class TaskOut(BaseModel):                         # response DTO
    id: int
    title: str
    is_done: bool
    model_config = ConfigDict(from_attributes=True)   # read fields off the ORM object


@app.post("/tasks", status_code=201, response_model=TaskOut)
def create_task(task: TaskIn, db: Session = Depends(get_db)):
    row = models.Task(title=task.title, is_done=task.is_done)
    db.add(row)
    db.commit()
    db.refresh(row)                               # reload the DB-assigned id
    return row


@app.get("/tasks", response_model=list[TaskOut])
def list_tasks(db: Session = Depends(get_db)):
    return db.query(models.Task).all()
