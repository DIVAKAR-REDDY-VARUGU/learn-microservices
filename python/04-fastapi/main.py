import threading
import time
from fastapi import FastAPI,responses,HTTPException,Depends
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


#  Middleware 
@app.middleware("http")
async def loggingMiddleware(req,next):
    print("entered middleware")
    response=await next(req)
    print("done with operation")
    return response


# Guard 
def tokenValidation(token:str):
    if(token!="secret"):
        raise HTTPException(401, "Invalid token")
    return {"user": "Divakar"}   #get from redis 



# Interceptor 
def timer():
    start=time.time()

    try:
        yield
    finally:
        end = time.time()
        print("Request took", end-start)


# DB connection 
def get_db():

    print("Open DB")

    db = "connection"

    try:
        yield db

    finally:
        print("Close DB")




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
def create(
    task:TaskCreateDto,
    user=Depends(tokenValidation),
    db=Depends(get_db),
    _: None = Depends(timer)
    ):
    task={
        "id":nextId(),
        **task.model_dump()
    }
    tasks.append(task)
    return {"message":"task created successfully","task":task}

def pagination(skip: int = 0, limit: int = 10):   # a dependency = a function
    return {"skip": skip, "limit": limit}

@app.get("/tasks")
def getTasks(page:dict=Depends(pagination)):
    return tasks[page["skip"] : page["skip"] + page["limit"]]

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

































