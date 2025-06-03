<template>
  <div class="container mx-auto p-4">
    <h1 class="text-3xl font-bold mb-6">Processing Results</h1>
    
    <div class="bg-white rounded-xl shadow-md overflow-hidden">
      <div class="border-b border-gray-200">
        <div class="p-6">
          <h2 class="text-lg font-medium text-gray-900">Invoice Information</h2>
          <p class="mt-1 text-sm text-gray-500">Details extracted from your invoice</p>
        </div>
      </div>
      
      <div class="p-6">
        <div v-if="loading" class="flex justify-center items-center py-10">
          <svg class="animate-spin h-8 w-8 text-blue-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <span class="ml-3 text-sm text-gray-500">Loading results...</span>
        </div>
        
        <div v-else-if="error" class="text-center py-10">
          <svg class="mx-auto h-12 w-12 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <h3 class="mt-2 text-sm font-medium text-gray-900">Error Loading Results</h3>
          <p class="mt-1 text-sm text-gray-500">{{ error }}</p>
          <div class="mt-6">
            <button 
              @click="router.push('/')" 
              class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              Go back
            </button>
          </div>
        </div>
        
        <div v-else-if="results" class="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <div v-for="(value, key) in formattedResults" :key="key" class="flex flex-col">
            <dt class="text-sm font-medium text-gray-500">{{ formatKey(key) }}</dt>
            <dd class="mt-1 text-sm text-gray-900">{{ value || 'N/A' }}</dd>
          </div>
        </div>
        
        <div class="mt-8 border-t border-gray-200 pt-6">
          <div class="flex justify-between">
            <a 
              :href="downloadUrl" 
              target="_blank" 
              class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500"
              v-if="downloadUrl"
            >
              <svg class="mr-2 h-5 w-5" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M3 17a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zm3.293-7.707a1 1 0 011.414 0L9 10.586V3a1 1 0 112 0v7.586l1.293-1.293a1 1 0 111.414 1.414l-3 3a1 1 0 01-1.414 0l-3-3a1 1 0 010-1.414z" clip-rule="evenodd" />
              </svg>
              Download Excel
            </a>
            <button 
              @click="router.push('/')" 
              class="inline-flex items-center px-4 py-2 border border-gray-300 shadow-sm text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              Process Another Invoice
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import api from '@/services/api'

const router = useRouter()
const route = useRoute()
const results = ref(null)
const loading = ref(true)
const error = ref(null)
const downloadUrl = ref(null)

const formattedResults = computed(() => {
  if (!results.value) return {}
  return {
    invoiceNumber: results.value.invoiceNumber,
    date: formatDate(results.value.invoiceDate),
    vendorName: results.value.vendorName,
    vendorVatNumber: results.value.vendorVatNumber,
    clientName: results.value.clientName,
    clientVatNumber: results.value.clientVatNumber,
    totalAmount: formatCurrency(results.value.totalAmount, results.value.currency),
    totalNetAmount: formatCurrency(results.value.netAmount, results.value.currency),
    totalVatAmount: formatCurrency(results.value.vatAmount, results.value.currency),
    currency: results.value.currency,
    description: results.value.description
  }
})

const formatKey = (key) => {
  return key
    .replace(/([A-Z])/g, ' $1')
    .replace(/^./, (str) => str.toUpperCase())
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString()
}

const formatCurrency = (amount, currency) => {
  if (amount == null) return ''
  return new Intl.NumberFormat(undefined, {
    style: 'currency',
    currency: currency || 'EUR'
  }).format(amount)
}

const loadResults = async () => {
  if (!route.query.jobId) {
    router.push('/')
    return
  }
  
  loading.value = true
  try {
    const response = await api.getResults(route.query.jobId)
    results.value = response.data
    downloadUrl.value = results.value.excelUrl
  } catch (err) {
    error.value = err.response?.data?.message || 'Failed to load results'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadResults()
})
</script>