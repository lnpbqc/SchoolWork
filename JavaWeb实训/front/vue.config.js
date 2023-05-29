const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    proxy: {
      '/api': {
        target: 'http://localhost:8888', // 设置代理目标
        // target: 'http://103.140.229.45:8888', // 设置代理目标
        ws: false, // 是否代理 WebSockets
        changeOrigin: true,// 允许跨域
        // eslint-disable-next-line no-mixed-spaces-and-tabs
        pathRewrite:{
          '^/api':''
          // eslint-disable-next-line no-mixed-spaces-and-tabs
        }
      }
    }
  }
})
