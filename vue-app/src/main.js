import { createApp } from 'vue'
import App from './App.vue'
import router from './router.js'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ELIcons from '@element-plus/icons-vue'


const app  = createApp(App)
for (let iconName in ELIcons) {
    app.component(iconName, ELIcons[iconName])
}
app.use(router)
app.use(ElementPlus)
app.mount('#app')
