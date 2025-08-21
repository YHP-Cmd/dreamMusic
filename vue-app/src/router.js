//(1)引入路由依赖
import {createRouter, createWebHashHistory} from 'vue-router'
//(2)配置组件访问的路径
const routes=[
    {
        path:'/',
        component:()=>import('./login/login.vue')
    },
    {
        path: '/regis',
        component:()=>import('./login/regis.vue')
    },
    {
        path: '/temp',
        component:()=>import('./temp.vue')
    },
    {
        path: '/test',
        component:()=>import('./test.vue')
    },
    {
        path: '/home',
        component:()=>import('./layouts/home.vue'),
        children:[
            {
                path:'/recommend',
                component:()=>import('./components/recommend.vue')
            },
            {
                path:'/my',
                component:()=>import('./components/my.vue')
            },
            {
                path:'/playlist',
                component:()=>import('./components/playlist.vue')
            },
            {
                path:'/ranking',
                component:()=>import('./components/ranking.vue')
            },
            {
                path:'/select',
                component:()=>import('./components/select.vue')
            },
            {
                path:'/singer',
                component:()=>import('./components/singer.vue')
            },
            {
                path:'/singerInfo',
                component:()=>import('./info/singerInfo.vue'),
                children:[
                    {
                        path:'/songInfo',
                        component:()=>import('./info/songInfo.vue')
                    },
                    {
                        path: '/album',
                        component:()=>import('./info/album.vue')
                    },
                    {
                        path: '/albumInfo',
                        component:()=>import('./info/albumInfo.vue')
                    }
                ]
            }
        ]
    }


]
//(3)创建路由实例
const router = createRouter({
    history: createWebHashHistory(),
    routes
})
//(4)将路由实例导出
export default router;
