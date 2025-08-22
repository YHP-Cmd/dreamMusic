import { createApp } from 'vue'
import App from './App.vue'
import router from './router.js'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ELIcons from '@element-plus/icons-vue'
import {createPinia} from "pinia";
const pinia = createPinia()

const app  = createApp(App)
for (let iconName in ELIcons) {
    app.component(iconName, ELIcons[iconName])
}
app.use(router)
app.use(ElementPlus)
app.use(pinia)
app.mount('#app')
