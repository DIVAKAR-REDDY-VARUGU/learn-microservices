from fastapi import FastAPI

app=FastAPI()


# GET / → returns a {"message": ...} dict.
@app.get("/")
def root():
    return {"message":"go to http://127.0.0.1:8000/docs for docs"}



# GET /tasks/{task_id} with task_id: int → returns the id.
@app.get("/tasks/{taskId}")
def getTask(taskId:int):
    return {"task":{"id":taskId,"title":f" tasks {taskId} title"}}


# GET /hello with a query param name: str = "world".
@app.get("/hello")
def hello(name:str="default str value"):
    return f"greetings from {name}"




# Run uvicorn main:app --reload, then open /, /docs, and /tasks/7 in your browser.



































