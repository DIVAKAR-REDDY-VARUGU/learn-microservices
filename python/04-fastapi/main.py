from fastapi import FastAPI
from pydantic import BaseModel

'''

creating task create DTO

'''
class TaskCreateDto(BaseModel):
    id:int
    title:str
    isDone:bool

tasks:list[dict]=[]


app=FastAPI()


# GET / → returns a {"message": ...} dict.
@app.get("/")
def root():
    return {"message":"go to http://127.0.0.1:8000/docs for docs"}



# GET /tasks/{task_id} with task_id: int → returns the id.
@app.get("/tasks/{taskId}")
def getTask(taskId:int):
    filteredTask=list(filter(lambda task:task["id"]==taskId,tasks))
    if len(filteredTask)==0:
        return {"message":"task not found"}
    return filteredTask[0]


# GET /hello with a query param name: str = "world".
@app.get("/hello")
def hello(name:str="default str value"):
    return f"greetings from {name}"


@app.post("/create")
def create(task:TaskCreateDto):
    task={
        "id":task.id,
        "title":task.title,
        "isDone":task.isDone
    }
    tasks.append(task)
    return {"message":"task created successfully","task":task}

@app.get("/tasks")
def getTasks():
    return tasks


# Run uvicorn main:app --reload, then open /, /docs, and /tasks/7 in your browser.



































