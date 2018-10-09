function init() {
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

          let feedback = document.querySelector('div[role=status]');
          feedback.innerHTML = `${name} added.`
        });
      } else {
        let alert = document.querySelector('div[role=alert]');
        alert.innerHTML = `<p>Failed to add "${name}", server returned ${res.status}.</p>`
      }
    })
    .catch(e => {
      let alert = document.querySelector('div[role=alert]');
      alert.innerHTML = `<p>Failed to add "${name}", an unexpected error occurred.</p>`
      console.error(`Failed to add "${name}" - ${e}`)
    });
  };
}