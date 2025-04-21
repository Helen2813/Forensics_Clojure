1. Project Overview
This project focuses on developing text forensics tools in Clojure using OpenNLP to perform criminal
profiling and authorship analysis. By analyzing text through lexical, syntactic, semantic, and stylistic
techniques, the system generates meaningful insights for forensic investigations.

Ensure high accuracy and originality in forensic profiling by leveraging OpenNLP for parsing and
analysis.

2. Tools, Technologies, and Dataset
Tools and Technologies:
Programming Language: Clojure
NLP Library: OpenNLP ([clojure-opennlp "0.5.0"])
Dependency Management: Leiningen
IDE/Editor: IntelliJ IDEA / Emacs
File Formats: Console outputs and CSV reports (report_output.csv)
Dataset Description:
Training and Test Split:
- 80% for training
- 20% for testing
Total Test Samples: 40
Validation Accuracy: 60% (24 correct predictions out of 40)

3. Dataset Setup Instructions
📚 Dataset Setup Instructions
This project uses the Blog Authorship Corpus dataset from Kaggle. Due to size constraints, the dataset is not
included in this repository. Follow the steps below to download and prepare the data for local use.
🔗 Step 1: Download the Dataset
Visit the dataset page on Kaggle:
👉 https://www.kaggle.com/datasets/rtatman/blog-authorship-corpus/data
Click Download to get the ZIP archive.
The file you need will likely be named something like:
blogtext.csv or blogtext.csv.zip
🛠 Step 2: Rename the File
After downloading and unzipping, rename the CSV file to exactly:
dataset.csv
This is important! The processing code looks specifically for that filename.
📁 Step 3: Move the File to the Correct Directory
Move dataset.csv into the following directory inside the project:resources/dataset/raw/ If these folders
do not exist, create them manually.
▶ Step 4: Run the Preprocessing Step
Once the file is in place, run the project to preprocess the data:lein run This will automatically:
Rename the "sign" column to "author"
Clean the "text" column
Save the cleaned dataset as: resources/dataset/processed/dataset_clean.csv

4. NLP Integration and Analysis
NLP Integration:
The project utilizes OpenNLP to perform various NLP tasks such as:
Tokenization: Splitting text into individual words.
POS Tagging: Assigning part-of-speech tags to words.
Syntactic Parsing: Generating parse trees for sentence structure.
Key Features and Analysis:
4.1 Lexical Analysis
Extracts word metrics and analyzes lexical patterns.
Computes word count, vocabulary diversity, and frequency distributions.
4.2 Syntactic Analysis
Uses OpenNLP to parse sentences and generate syntactic structures.
Analyzes parts of speech and grammatical patterns to create author fingerprints.
4.3 Semantic Analysis
Converts words to lowercase, removes punctuation, and calculates similarity using Jaccard
similarity.
Compares documents for similarity in word and POS patterns to detect plagiarism and author
attribution.
4.4 Stylistic Analysis
Analyzes sentence and word metrics to detect author style.

Calculates readability scores and punctuation usage to identify stylistic anomalies.

5. Authorship Analysis Workflow
Workflow Steps:
Sentence Detection & Tokenization: Processes text and converts it into tokens.
Lowercase Conversion & Stripping: Standardizes text for consistency.
Word Frequency Mapping: Generates frequency distributions for tokens.
Cosine Similarity for Authorship Verification: Uses a threshold of 0.5 to determine authorship
similarity.

6. Results and Validation
Testing Accuracy: 60%
Evaluation Metric:
Accuracy = (Correct Predictions / Total test samples) x 100
Output Formats:
Console Format: Displays analysis results in a readable format.
CSV Format: report_output.csv generated for external analysis.

7. Conclusion
The project successfully developed text forensics tools in Clojure using OpenNLP to analyze and profile text
for forensic applications. Key accomplishments include:
Lexical, Syntactic, Semantic, and Stylistic Analysis: Extraction of detailed linguistic features for
criminal profiling.
Authorship Verification: Application of tokenization and cosine similarity with a validation accuracy of
60%.
Reporting & Visualization: Generation of structured reports in console and CSV formats for external
analysis.
The modular design of the system allows future enhancements to expand the scope of forensic analysis and
improve accuracy in identifying authorship and stylistic traits.
