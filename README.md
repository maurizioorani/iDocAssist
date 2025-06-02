# DocAssist - Automated Invoice Processing System

![version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![license](https://img.shields.io/badge/license-MIT-green.svg)

DocAssist is a powerful and secure document processing system designed to automate invoice data extraction and analysis. It combines OCR (Optical Character Recognition) technology with local AI-powered data extraction to transform invoice documents into structured data and generate comprehensive Excel reports, all while maintaining strict data privacy through 100% local processing.

## 🚀 Features

- **Intelligent Document Processing**: Extract text from invoices using OCR technology
- **AI-Powered Data Extraction**: Utilize Large Language Models to extract structured data from invoices
- **Multi-Format Support**: Process PDF and image-based invoice files (PNG, JPG)
- **Multi-Language Support**: Process documents in English and Italian with dedicated language models
- **Excel Report Generation**:
  - Generate detailed Excel reports with extracted invoice data
  - Create consolidated reports from multiple invoices in a single Excel file
  - Include summary statistics with totals, vendor breakdowns, and currency distribution
- **RESTful API**: Simple interface for integration with other systems
- **Batch Processing**: Process multiple invoices in a single request
- **Local Processing & Security**: All data is processed locally without sending sensitive information to external services
- **Persistent Storage**: Optional database integration for document archiving and retrieval

## 📋 Requirements

- Java 17 or higher
- Maven 3.6 or higher
- Tesseract OCR (with English and Italian language packs)
- Ollama (for AI-powered extraction)
- PostgreSQL database (for document storage)
- Docker (optional, for containerized deployment)

For detailed database setup instructions, please refer to the [DATABASE_README.md](DATABASE_README.md) file.

## 🛠️ Installation

### 1. Clone the Repository

```bash
git clone https://github.com/maurizioorani/iDocAssist.git
```

### 2. Install Tesseract OCR

#### Windows
1. Download and install Tesseract from [UB Mannheim](https://github.com/UB-Mannheim/tesseract/wiki)
2. Add Tesseract to your PATH
3. Download language data files (eng.traineddata, ita.traineddata) and place them in the `tessdata` directory

#### Linux
```bash
sudo apt-get update
sudo apt-get install tesseract-ocr
sudo apt-get install tesseract-ocr-eng tesseract-ocr-ita
```

### 3. Setup Ollama (for AI-powered extraction)

1. Download and install [Ollama](https://ollama.ai/)
2. Run a compatible LLM model (recommended: mistral or llama2)
```bash
ollama run mistral
```

### 4. Configure the Application

1. Edit `src/main/resources/application.properties` to set:
   - Tesseract OCR path
   - Ollama API URL
   - Preferred model
   - Database settings (if needed)

Example configuration:
```properties
# OCR Settings
app.ocr.tesseract-path=/usr/bin/tesseract
app.ocr.tessdata-path=/path/to/tessdata

# Ollama Settings
app.ollama.base-url=http://localhost:11434
app.ollama.model-name=mistral

# Output Directory
app.output.directory=output
```

### 5. Build the Application

```bash
mvn clean package
```

## 🏃‍♂️ Running the Application

### Using Maven

```bash
mvn spring-boot:run
```

### Using JAR File

```bash
java -jar target/docassist-0.0.1-SNAPSHOT.jar
```

### Using the Provided Batch Script

```bash
start-app.bat
```

## 🔍 API Usage

### Health Check
```
GET /api/invoice/health
```

### OCR Only
```
POST /api/invoice/ocr-only
Content-Type: multipart/form-data
Form params:
  - file: Invoice file (PDF, PNG, JPG)
  - language: OCR language (eng, ita)
```

### Process Invoice
```
POST /api/invoice/process
Content-Type: multipart/form-data
Form params:
  - file: Invoice file (PDF, PNG, JPG)
  - language: OCR language (eng, ita)
```

### Generate Excel for Single Invoice
```
POST /api/invoice/process-to-excel
Content-Type: multipart/form-data
Form params:
  - file: Invoice file (PDF, PNG, JPG)
  - language: OCR language (eng, ita)
  - outputPath: (optional) Custom output path
```

### Generate Consolidated Excel from Multiple Invoices
```
POST /api/invoice/process-batch-to-excel
Content-Type: multipart/form-data
Form params:
  - files: Multiple invoice files
  - language: OCR language (eng, ita)
  - outputPath: (optional) Custom output path
```

## 🧪 Testing

### Run Tests
```bash
mvn test
```

### Testing Scripts
The `test_batches` directory contains several batch scripts to test different aspects of the application:
- `test-endpoints.bat`: Test all API endpoints
- `test-file-uploads.bat`: Test file upload functionality
- `test-batch-excel.bat`: Test batch Excel generation with multiple invoices
- `test-excel-generation.bat`: Test Excel generation for individual invoices

## 🔒 Security & Data Privacy

DocAssist is designed with data privacy and security in mind:

- **100% Local Processing**: All document processing occurs locally on your machine
- **No External API Dependencies**: Invoice data is never sent to external cloud services
- **On-premises AI**: Uses Ollama for local LLM execution without internet connectivity requirements
- **Data Persistence**: Optional database storage is self-hosted and can be configured with authentication
- **No Internet Required**: Once installed, the application can run in airgapped environments

For database security settings and configuration, please refer to the [DATABASE_README.md](DATABASE_README.md) file.

## 📝 Example Output

The application generates Excel files with the following structure:

1. **Invoice Data Sheet**:
   - Source File
   - Invoice Number
   - Invoice Date
   - Vendor Name
   - Vendor VAT Number
   - Client Name
   - Client VAT Number
   - Net Amount
   - VAT Amount
   - Total Amount
   - Currency
   - Description
   - Processing Notes

2. **Summary Sheet** (for batch processing):
   - Total Invoices
   - Total Net Amount
   - Total VAT Amount
   - Total Gross Amount
   - Vendor Breakdown (count and amount by vendor)
   - Currency Breakdown (count by currency)

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📧 Contact

For questions or support, please contact [orani.maurizio@gmail.com]
