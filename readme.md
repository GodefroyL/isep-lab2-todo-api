# Lab #2: Architecting a Todo app

In this lab, you'll learn how to design and structure a simple Web API for a "Todo List" application, with a focus on applying clean architectural principles.

By the end of the lab, you’ll have a functional and well-structured API that supports:
- Creating a Todo
- Listing all Todos

## 🔍 Lab overview
- We'll begin by building a basic working API, storing Todos in memory, without worrying about architecture.
- Next, we’ll refactor the project into a 3-tier architecture to improve separation of concerns.
- We'll then evolve the architecture into a more scalable and maintainable Hexagonal Architecture (also known as Ports and Adapters).
- Using this new structure, we'll implement an alternative org.isep.cleancode.persistence method using CSV files.
- Finally, we’ll make the app configurable so it can switch between in-memory and CSV-based storage at startup.

## 📥 Submission

Please submit your completed lab by May 13th 23:59, via email. You will receive a bonus point if you submit your work through a public Git repository (e.g., GitHub, GitLab) and share the link in your email. Make sure your repository is well-organized and clearly reflects the steps of the lab.

## 📝 Grading

How your lab will graded (/20):
- The **API works** : **0-2** pts
- **Business rules** are respected : **0-2** pts
- Code **design** follows clean code rules seen in class : **0-4** pts
- Project **architecture** (depending on which step has been reached) (non-cumulative)
  - Step 2 completed : **0-4** pts
  - Step 3 completed : **0-9** pts
  - Step 4 completed : **0-12** pts

Keep in mind: This lab is mostly about **architecting** a project. Do not spend too much time on implementation details.

## Before you start

It is recommended that you <ins>read again the `Architecture` section of the [Clean Code lecture](lecture.md) .</ins>

It is strongly recommended to use the provided starter Java project, which includes a pre-configured API to help you get up and running quickly.

However, if you are already familiar with building Web APIs, you are free to make a project from scratch in the OOP language of your choice among :
- Java
- Typescript (not Javascript)
- PHP
- C#
- C++

## 📏 Requirements of the Todo App :

Here are the business rules for the Todo App :

A **Todo** has a required unique **name**, and an optional **due date**. It must follow the following rules :
- the **name** is **required** and must be **shorter than 64 characters**
- the **name should be unique**. Two todos cannot have the same name.
- the **due date** can either be **left void** or be a **valid date**

## Step 1 : Make a basic Web API (~30 min)

For this first step, we will simply make 2 basic endpoints, without caring much about the architecture (for now).

- The first endpoint should create a Todo, following the requirements above.
- The second one should list them all.

For now, Todos will be stored "in memory", i.e. in a basic data structure of your choice, such as a list for example. Make sure this data structure is instantiated only once and shared by the 2 endpoints. The starter project already has it covered.

Since they are stored in memory, Todos will be "lost" when the program shuts down. It's ok for now, we'll deal with proper org.isep.cleancode.persistence later.

Here is an example way of designing the 2 endpoints

`POST /todos`
- Creates a Todo
- Returns:
  - 201 if ok
  - 400 if the business rules are not respected

`GET /todos`
- Returns a 200 with all todos.

The starter project already have most of the API covered. You only have to <ins>complete the `Todo` and `TodoController` classes to ensure that the business rules listed earlier are respected</ins>.

Remember to <ins>test your API</ins>. Try to create several Todos and to list them.

# Step 2 : Split the code into a 3 tier architecture (~30 min)

Now that we have a working Web API for our Todos, we will focus on its **architecture**. In this step, we will turn our "1-tier" architecture into a **3-tier architecture**. It will allow us to clearly separate the code according to its responsibilities

For that, you will <ins>split and move yur code in 3 distinct layers</ins> :
- **DataPersistence** layer
  - Contains the code that stores and retrieves Todos in and from an in-memory collection of Todos
  - <ins>It may contain, for example, a `TodoRepository` class</ins>
- **Application** layer
  - Contains the Business logic and rules, the use cases of the Application.
  - <ins>It may contain, for example, a `TodoManager` class which instantiates a `TodoRepository` object to work with</ins>. The `TodoManager` class should ensure business rules and throw exceptions when they are not respected, or use an alternative way, such as error codes.  
- **Presentation** layer
  - Contains the API endpoints
  - <ins>It may contain the `TodoController` class, which may instantiate a `TodoManager` object to work with</ins>

To achieve this, you may <ins>separate your code into 3 different packages</ins>. Or, even better (but you are not required to), into different independant modules.

The code of the presentation layer should be calling the code of the application layer, which should be calling the code of the data org.isep.cleancode.persistence layer.

To keep this lab simple, the Todo class will be shared across all layers. In a more robust architecture, it’s considered good practice to define distinct models for each layer—for example, using Entities in the domain layer and DTOs (Data Transfer Objects) in the presentation layer. However, since the goal of this lab is to focus on architectural fundamentals, we’ll skip that separation for now. If you're curious, feel free to explore more about Entities, DTOs, and how separating models per layer improves decoupling and maintainability.

By the end of this step, your project hierarchy should look similar to this :

```
- Main.java
- Todo.java
- /presentation
    - TodoController.java
- /application
    - TodoManager.java
- /org.isep.cleancode.persistence
    - TodoRepository.java
```

<ins>Test your API</ins> and make sure it still works !

This architecture is a good start. It makes the code a bit easier to maintain. But it is still not very scalable, nor is it extensible. What if we want to add another means of org.isep.cleancode.persistence for our Todos ? Such as a proper database, or CSV files ? We would be forced to rewrite the TodoRepository. But what if we also want to keep the in-memory org.isep.cleancode.persistence implementation for testing purpose ?

In the next step, we will make our project more extensible by making a more complex, but more robust architecture.

# Step 3 : Turn the 3 tier architecture into an Hexagonal Architecture (~1h)

In this next step, we will transform our 3 tier architecture into an **Hexagonal architecture** (also known as **Ports and Adapters** architecture). This will enable easier testing and allow swapping implementations without changing business logic.

The first thing we are going to do is to get rid of the **dependency** that the Application layer has on the Data Persistence layer, and actually **revert** it.

By the end of this step, the architecture should have gone from :

`(Presentation) => (Application) => (Data Persistence)`

to

`(Presentation) => (Application) <= (Data Persistence)`

To do so, <ins>the Application Layer will declare an **Outbound Port** (an interface)</ins> for the Data Persistence to **adapt** (implement) :

/application/ITodoRepository.java
```java 
public interface ITodoRepository {
  void addTodo(Todo todo);
  List<Todo> getAllTodos();
}
```

Then, <ins>make the `TodoRepository` class implement this interface</ins>. The class `TodoRepository` is now an **adapter** for the `ITodoRepository` **port**.

Now the `TodoManager` class from the Application Layer will work with an `ITodoRepository` instead of a `TodoRepository`. And we will apply the 5th SOLID principle: "Dependency Inversion". <ins>Update `TodoManager` to accept an `ITodoRepository` through its constructor, and use it instead of the `TodoRepository`</ins>. In doing so, the Application Layer is not responsible anymore for choosing the implementation of the `ITodoRepository`, the only thing that it cares about is to receive any implementation of this interface.

Now your code in the Presentation Layer should have errors because instantiating a `TodoManager` requires to give it an implementation of `ITodoRepository`. Well, <ins>let's just instantiate a `TodoRepository` and pass it to the `TodoManager` constructor<ins>.

/presentation/TodoController.java
```java 
private final TodoManager todoManager = new TodoManager(new TodoRepository());
```

<ins>Optional</ins>: we could also go further and have our `TodoController` accept an `ITodoRespository` in its constructor rather than letting it decide the implementation it is using. Then the TodoRepository would be instantiated in the `Main` class and injected to `TodoController` there.

# Step 4 : Implement another adapter for the ITodoRepository port. (~45 min)

One of the purposes of the Hexagonal architecture is to make the code modular. So far, the Application gets **injected** a `TodoRepository`, which is an in-memory implementation of the `ITodoRepository`. In this next step, we will create another implentation of the `ITodoRepository`, which will be using a csv file to store the todos. And we will inject it instead of the in memory implementation.

The first thing we are going to do is to <ins>rename the `TodoRepository` class to `TodoInMemoryRepository`</ins> in order to clarify what this implementation is doing.

And then we will <ins>create another implementation of `ITodoRepository` which we are going to call `TodoCsvFilesRepository`</ins>. It should read and write the Todos from and to a csv file located anywhere on your system (preferably somewhere in the AppData folder by using `System.getenv("APPDATA")`).

It is recommended that <ins>the 2 implementations (InMemory and Csv) live in two distinct packages</ins> and that they do not share code. The cleaner way to enforce this would be to make them into 2 distinct modules (but once again, you are not required to).

Here is an example for the project Hierarchy that you may have at this point :

```
- Main.java
- Todo.java
- /presentation
    - TodoController.java
- /application
    - TodoManager.java
    - ITodoRepository.java
- /org.isep.cleancode.persistence
    - /inmemory
        - TodoInMemoryRepository.Java
    - /csvfiles
        - TodoCsvFilesRepository.Java
```

Now the dependency graph of the project looks like this :

`(Presentation) => (Application) <= (In Memory Data Persistence) / (Csv Files Data Persistence)`

Now, <ins>in the Presentation Layer, make sure to instantiate a `TodoCsvFilesRepository` instead of a `TodoInMemoryRepository`</ins>, and test your API again !

# Step 5 (Bonus) : Make a way to select adapter at startup time (~30 min)

The goal of this optional step is to make your program configurable so you can either start it with an in-memory or a csv files implementation of the repository. Swapping from to the other should not require to rebuild the code.

Here are 2 ways to achieve this, you may **choose one of them** (not both) and <ins>implement it to get bonus points</ins> !
- (+1 bonus point) use a command line argument that specifies the repository implementation (example: repo="INMEMORY")
- (+2 bonus points) use a configuration file (provided via command-line argument) to specify the repository implementation.