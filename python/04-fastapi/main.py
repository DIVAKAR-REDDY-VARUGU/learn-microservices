import threading

from fastapi import FastAPI,responses,HTTPException
from pydantic import BaseModel

lock = threading.Lock()

'''
creating task create DTO and update Task DTO
'''
class TaskCreateDto(BaseModel):
    title:str
    isDone:bool

class TaskPatchDto(BaseModel):
    title:str|None=None
    isDone:bool|None=None



id=1
def nextId():
    global id
    with lock:
        id+=1
        return id
    
tasks:list[dict]=[]


app=FastAPI()




def findTask(id:int):
    for task in tasks:
        if task["id"]==id:
            return task
    raise HTTPException(status_code=404,detail="Task Not Found")











# GET / → returns a {"message": ...} dict.
@app.get("/")
def root():
    return responses.RedirectResponse(url="/docs")



@app.post("/create")
def create(task:TaskCreateDto):
    task={
        "id":nextId(),
        **task.model_dump()
    }
    tasks.append(task)
    return {"message":"task created successfully","task":task}

@app.get("/tasks")
def getTasks():
    return tasks

# GET /tasks/{task_id} with task_id: int → returns the id.
@app.get("/tasks/{taskId}")
def getTask(taskId:int):
    return findTask(taskId)


@app.put("/tasks/{taskId}")
def updateTask(taskId:int,body:TaskCreateDto):
    task=findTask(taskId)
    task.update(body.model_dump())
    return task

@app.patch("/tasks/{taskId}")
def patchTask(taskId:int,body:TaskPatchDto):
    task=findTask(taskId)
    task.update(body.model_dump(exclude_unset=True))
    return task

@app.delete("/tasks/{task_id}", status_code=204)
def delete_task(task_id: int):
    task = findTask(task_id)
    tasks.remove(task)
    return None                               







# Run uvicorn main:app --reload, then open /, /docs, and /tasks/7 in your browser.

































