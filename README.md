# DocAssist - Automated Invoice Processing System

![version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![license](https://img.shields.io/badge/license-MIT-green.svg)

DocAssist is a powerful and secure document processing system designed to automate invoice data extraction and analysis. It combines OCR (Optical Character Recognition) technology with local AI-powered data extraction to transform invoice documents into structured data and generate comprehensive Excel reports, all while maintaining strict data privacy through 100% local processing.

## 🚀 Features

- **Web User Interface**: A modern and intuitive Vue.js frontend for easy interaction.
- **Intelligent Document Processing**: Extract text from invoices using OCR technology.
- **AI-Powered Data Extraction**: Utilize Large Language Models to extract structured data from invoices.
- **Multi-Format Support**: Process PDF and image-based invoice files (PNG, JPG).
- **Multi-Language Support**: Process documents in English and Italian with dedicated language models.
- **Excel Report Generation**:
  - Generate detailed Excel reports with extracted invoice data.
  - Create consolidated reports from multiple invoices in a single Excel file.
  - Include summary statistics with totals, vendor breakdowns, and currency distribution.
- **RESTful API**: Simple interface for integration with other systems.
- **Batch Processing**: Process multiple invoices in a single request.
- **Local Processing & Security**: All data is processed locally without sending sensitive information to external services.
- **Persistent Storage**: Optional database integration for document archiving and retrieval.

## 📋 Requirements

- Docker and Docker Compose (recommended for simplified setup)
- Alternatively, for manual setup:
  - Java 17 or higher
  - Maven 3.6 or higher
  - Tesseract OCR (with English and Italian language packs)
  - Ollama (for AI-powered extraction)
  - PostgreSQL database (for document storage)
  - Node.js (for frontend development/build)

For detailed database setup instructions, please refer to the [DATABASE_README.md](DATABASE_README.md) file.

## 🛠️ Installation & Setup (Recommended: Docker Compose)

The easiest way to get DocAssist up and running is by using Docker Compose, which will set up all necessary services (PostgreSQL, Ollama, Backend API, and Frontend UI) with a single command.

### 1. Clone the Repository

```bash
git clone https://github.com/maurizioorani/iDocAssist.git
cd iDocAssist
```

### 2. Build and Run with Docker Compose

Use the provided `docker-compose-fullstack.yml` to build and start all services. This process will also pull the required Ollama model (`llama3.2`) on the first run, which might take some time depending on your internet connection.

```bash
docker-compose -f docker-compose-fullstack.yml up --build -d
```

**Note**: The first time you run this, Docker will download the Ollama image and then the `llama3.2` model. This can take a while. You can monitor the progress by checking the logs:
```bash
docker logs -f docassist-ollama
```

### 3. Access the Application

Once all services are up and running (you can check `docker ps` to confirm), open your web browser and navigate to:

```
http://localhost:5173
```

You should see the DocAssist web interface.

---

## 🛠️ Manual Installation & Setup (Advanced)

If you prefer to set up each component manually, follow these steps:

### 1. Clone the Repository

```bash
git clone https://github.com/maurizioorani/iDocAssist.git
cd iDocAssist
```

### 2. Backend Setup (Spring Boot)

#### Requirements
- Java 17 or higher
- Maven 3.6 or higher
- Tesseract OCR (with English and Italian language packs)
- Ollama (for AI-powered extraction)
- PostgreSQL database

#### Steps
1.  **Install Tesseract OCR**:
    *   **Windows**: Download and install Tesseract from [UB Mannheim](https://github.com/UB-Mannheim/tesseract/wiki). Add Tesseract to your PATH. Download language data files (`eng.traineddata`, `ita.traineddata`) and place them in the `tessdata` directory.
    *   **Linux**:
        ```bash
        sudo apt-get update
        sudo apt-get install tesseract-ocr
        sudo apt-get install tesseract-ocr-eng tesseract-ocr-ita
        ```
2.  **Setup Ollama**:
    *   Download and install [Ollama](https://ollama.ai/).
    *   Run the `llama3.2` model:
        ```bash
        ollama run llama3.2
        ```
3.  **PostgreSQL Database**: Ensure your PostgreSQL database is running and accessible. Refer to [DATABASE_README.md](DATABASE_README.md) for setup.
4.  **Configure Application Properties**: Edit `src/main/resources/application.properties` to set:
    *   Tesseract OCR path
    *   Ollama API URL (e.g., `http://localhost:11434`)
    *   Ollama model name (e.g., `llama3.2`)
    *   PostgreSQL database settings (uncomment and configure `spring.datasource` properties).
5.  **Build the Backend**:
    ```bash
    mvn clean package
    ```
6.  **Run the Backend**:
    ```bash
    mvn spring-boot:run
    ```
    The backend API will be available at `http://localhost:8080`.

### 3. Frontend Setup (Vue.js)

#### Requirements
- Node.js (LTS version recommended)
- npm or Yarn

#### Steps
1.  **Navigate to Frontend Directory**:
    ```bash
    cd frontend
    ```
2.  **Install Dependencies**:
    ```bash
    npm install
    ```
3.  **Configure API Endpoint**: The frontend is configured to connect to `http://localhost:8080/api` by default. If your backend is running on a different host or port, you might need to adjust `frontend/src/services/api.js` or set the `VITE_API_BASE_URL` environment variable.
4.  **Run the Frontend**:
    ```bash
    npm run dev
    ```
    The frontend development server will start, usually accessible at `http://localhost:5173`.

---

## 🏃‍♂️ Running the Application (Manual)

### Backend Only

#### Using Maven
```bash
mvn spring-boot:run
```

#### Using JAR File
```bash
java -jar target/docassist-0.0.1-SNAPSHOT.jar
```

#### Using the Provided Batch Script (Windows)
```bash
start-app.bat
```

### Frontend Only (after Backend is running)

#### Using npm
```bash
cd frontend
npm run dev
```

---

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

### Get Invoice History
```
GET /api/invoice/history
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
