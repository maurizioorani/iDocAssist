import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'multipart/form-data'
  }
})

export default {
  processToExcel(file) {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('/invoice/process-to-excel', formData)
  },
  processOCR(file) {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('/invoice/ocr-only', formData)
  },
  getProcessingStatus(jobId) {
    return api.get(`/invoice/status/${jobId}`)
  },
  getResults(jobId) {
    return api.get(`/invoice/results/${jobId}`)
  },
  getInvoices() {
    return api.get('/invoice/history')
  }
}