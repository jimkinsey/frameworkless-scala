function init() {
  initAdding();
  initRemoving(...document.querySelectorAll('li'));
}

function initAdding() {
  document.querySelector('form[name=add]').onsubmit = (event) => {
    event.preventDefault();

    let name = document.querySelector('input[name=name]').value;

    fetch('/todos', {
      method: 'POST',
      body: name
    })
    .then(res => {
      if (res.ok) {
        return res.text().then(id => {
          let number = document.querySelectorAll('li').length;

          let template = document.querySelector('#item-template');
          let fragment = document.importNode(template.content, true);

          let input = fragment.querySelector('input');
          input.name = id;
          input.id = `todo-${number}`

          let label = fragment.querySelector('label');
          label.innerHTML = name;
          label.setAttribute('for', input.id);

          let button = fragment.querySelector('button');
          button.value = id;
          button.setAttribute('aria-label', `delete ${name}`);

          document.querySelector('ul').appendChild(fragment);

          initRemoving(document.querySelector('ul').lastElementChild);

          updateStatus(`${name} added.`);
        });
      } else {
        alertUser(`Failed to add "${name}", server returned ${res.status}.`);
      }
    })
    .catch(e => {
      alertUser(`Failed to add "${name}", an unexpected error occurred.`);
      console.error(`Failed to add "${name}" - ${e}`)
    });
  };
}

function initRemoving(...lis) {
  lis.forEach(li => {
    li.querySelector('button[name=delete]').onclick = (event) => {
      event.preventDefault();

      let id = li.querySelector('button[name=delete]').value;
      let name = li.querySelector('label').text;

      fetch(`/todos/${id}`, {
        method: 'DELETE',
        body: name
      })
      .then(res => {
        if (res.ok) {
          li.remove();
          updateStatus(`${name} deleted.`);
        } else {
          alertUser(`Failed to remove "${name}", server returned ${res.status}.`);
        }
      })
      .catch(e => {
        alertUser(`Failed to remove "${name}", an unexpected error occurred.`);
        console.error(`Failed to remove "${name}" - ${e}`)
      });
    }
  });
};

function alertUser(message) {
  let alert = document.querySelector('div[role=alert]');
  alert.innerHTML = `<p>${message}</p>`
}

function updateStatus(message) {
  let status = document.querySelector('div[role=status]');
  status.innerHTML = message;
}