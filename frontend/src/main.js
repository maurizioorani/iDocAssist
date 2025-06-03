import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router'
import * as components from './components' // Import all components

// PrimeVue imports
import PrimeVue from 'primevue/config';
import 'primevue/resources/themes/saga-blue/theme.css';
import 'primevue/resources/primevue.min.css';
import 'primeicons/primeicons.css';

const app = createApp(App)

app.use(PrimeVue)

// Register all components globally
for (const key in components) {
  app.component(key, components[key])
}

app.use(router).mount('#app')
