const items = Array.from(document.querySelectorAll('li')).map(item => {
    let id = item.querySelector('input[type=checkbox]').getAttribute('name');
    let name = item.querySelector('label').textContent;
    let done = item.querySelector('input').checked;

    return { id, name, done };
});

Array.from(document.querySelectorAll('li')).map(li => li.remove());

document.querySelector('ul').innerHTML = `
<todo-item 
  v-for="todo in todos"
  v-bind:todo="todo"
  v-bind:key="todo.id">  
</todo-item>
`;

document.querySelector('section').id = "app";

Vue.component('todo-item', {
   props: ['todo'],
   template: `
     <li>
       <input type="checkbox" :id="todo.id" v-model="todo.done" :name="todo.id" onclick="toggle(this);">
       <label :for="todo.id">{{ todo.name }}</label>
       <button aria-label="'delete ' + todo.name" name="delete" :value="todo.id">&times;</button>
     </li>
   `,
});

let app = new Vue({
    el: '#app',
    data: {
        todos: items,
    },
});

function alertUser(message) {
    let alert = document.querySelector('div[role=alert]');
    alert.lastElementChild.replaceWith(`<p>${message}</p>`);
}

function updateStatus(message) {
    let status = document.querySelector('div[role=status]');
    status.lastElementChild.replaceWith(`<p>${message}</p>`);
}

function toggle(checkbox) {
    const id = checkbox.getAttribute('name');

    fetch(`/todos/${id}/done`, {
        method: 'PUT',
        body: checkbox.checked.toString(),
    })
        .then(res => {
            if (res.ok) {
                updateStatus(`${name} ${checkbox.checked ? 'checked off' : 'unchecked'}.`);
            } else {
                checkbox.checked = !checkbox.checked;
                alertUser(`Failed to update "${name}", server returned ${res.status}.`);
            }
        })
        .catch(e => {
            checkbox.checked = !checkbox.checked;
            alertUser(`Failed to update "${name}", an unexpected error occurred.`);
            console.error(`Failed to update "${name}" - ${e}`)
        });
}