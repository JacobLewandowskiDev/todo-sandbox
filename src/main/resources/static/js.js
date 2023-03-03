const url = window.location.pathname;

let createPriority = document.getElementById('create-priority');
let selectedOption = createPriority.options[createPriority.selectedIndex].value; 

function createTodo() {
    const todo = {
        "id": null,
        'name': document.getElementById('create-todo-name').value,
        'description': document.getElementById('create-todo-desc').value,
        'priority': document.getElementById('create-priority').value,
        "nestedSteps": [
            {
                'id': 1,
                'name': 'test step',
                'description': 'step description'
            },
            {
                'id': 2,
                'name': 'step 2',
                'description': 'second step description'
            }  
        ]
    };
    fetch("/create-todo", {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(todo)
    })
    .then(response => {
        console.log("New todo created");
        console.log(todo);
        })
        .catch(e => console.log(e))
}

function getTodoList() {
    fetch("/todo-list", {
        method:'GET'
    }).then(response => response.json());
}

