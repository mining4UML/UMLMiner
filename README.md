# UML Miner - To make UML diagrams mining possible

UML Miner is a Visual Paradigm plug-in that makes possible mining all the UML diagrams realized in real-time.

It makes possible to:

* produce a Log Event written in the XES format that can be readily explored and analyzed upon the generation.
* export all the Logs generated in the XES format. The XES standard defines a grammar for a tag-based language whose aim is to provide designers of information systems with a unified and extensible methodology for capturing systems behaviors by means of event logs and event streams is defined in the XES standard.
* find out the actual modelling process which is happening inside a software project. To this aim, it allows to execute the [Declare Miner](https://www.sciencedirect.com/science/article/abs/pii/S0306437916306615?via%3Dihub) and [MINERful](https://dl.acm.org/doi/10.1145/2629447) techniques to process discovery.
* figure out if there are any deviations between the actual modelling process and the ideal modelling process (from the model). To this aim, it allows to execute the [Declare Analyzer](https://www.sciencedirect.com/science/article/abs/pii/S0957417416304390?via%3Dihub) and the [Declare Replayer](https://link.springer.com/chapter/10.1007%2F978-3-642-32885-5_6) methods to conformance checking. The conformance can be explored either by trace or by constraint.
* receive targeted and context-aware feedback through Large Language Models (LLMs). UML Miner supports both a remote Retrieval-Augmented Generation (RAG) + LLM configuration and a local LLM configuration. The feedback component supports learners in understanding modeling concepts, reflecting on their modeling process, and making informed design decisions throughout the construction of UML diagrams.

# How does it work?

For each "Visual Paradigm" project, UML Miner creates a unique log. A log is made up of all the working sessions on that project.

A working session of a project represents all the modeling events made across all the UML diagrams employed between an opening and the corresponding closure of the project.

Modeling events captured are related to:

* adding or removing UML diagrams in a project;
* adding or removing UML model elements in a project;
* adding or removing UML model elements in a diagram;
* adding, changing or removing any property of a model element contained in a project;
* adding, changing or removing any property of a model element contained in a diagram;
* adding, changing or removing any relationship between two or more UML model elements;
* adding, changing or removing any extension mechanism (stereotypes, constraints, tagged values).

# Local and remote feedback generation

UML Miner can generate feedback using two alternative AI configurations:

* **Local LLM mode**: UML Miner connects to a locally running OpenAI-compatible LLM server. This mode allows feedback generation without sending diagrams, logs, requirements, or student queries to external services.
* **Remote RAG + LLM mode**: UML Miner connects to a remote server that provides retrieval-augmented feedback generation.

The active provider can be selected from the UML Miner menu:

```text
AI Configuration
  - Use Local LLM
  - Use Remote RAG + LLM
  - Test AI Connection
```

The **Test AI Connection** command checks the currently selected provider and reports whether the connection is available.

## Using UML Miner with a local LLM through Ollama

To use UML Miner locally, install and run an OpenAI-compatible local LLM server. One possible option is [Ollama](https://ollama.com/).

### 1. Install Ollama

Download and install Ollama from:

```text
https://ollama.com/
```

### 2. Download a model

For example, to use Llama 3.1:

```bash
ollama pull llama3.1
```

### 3. Start Ollama

Ollama normally exposes a local server at:

```text
http://localhost:11434
```

UML Miner uses the OpenAI-compatible endpoint:

```text
http://localhost:11434/v1
```

### 4. Configure UML Miner

Create or update the file:

```text
llm.properties
```

inside the UML Miner plugin directory.

For local execution with Ollama:

```properties
llm.provider=local
llm.baseUrl=http://localhost:11434/v1
llm.model=llama3.1
llm.apiKey=
llm.temperature=0.2
```

### 5. Select the local provider

In Visual Paradigm, open the UML Miner toolbar and select:

```text
AI Configuration > Use Local LLM
```

Then run:

```text
AI Configuration > Test AI Connection
```

If the connection is successful, UML Miner is ready to generate feedback locally.

## Remote RAG + LLM configuration

To use the remote RAG-enhanced feedback service, set:

```properties
llm.provider=remote
```

The remote server endpoint is configured in:

```text
config.properties
```

for example:

```properties
BASE_URL=http://your-server-address/interact/
```

Then select:

```text
AI Configuration > Use Remote RAG + LLM
```

and test the connection through:

```text
AI Configuration > Test AI Connection
```

# Installation

UML Miner comes as a zip file. To install it in Visual Paradigm it is only necessary to select "Help > Install Plugin" from the application toolbar.

# Official Web site

https://sites.google.com/view/uml-miner/home-page

# Debugging configuration

Information on how to debug UML Miner in Eclipse IDE are available at https://knowhow.visual-paradigm.com/openapi/debug-plugins-eclipse/

# Team members

The team members, listed in alphabetical order, are:

* Pasquale Ardimento - Department of Computer Science, University of Bari Aldo Moro (responsible of project)
* Mario Luca Bernardi - Department of Engineering, Università degli Studi del Sannio, 82100 Benevento, Italy
* Marta Cimitile - Department of Law and Economics, Unitelma Sapienza University, 00161 Rome, Italy
* Michele Scalera - Department of Computer Science, University of Bari Aldo Moro

# Former Team Members

* Lerina Aversano - Department of Engineering, Università degli Studi del Sannio, 82100 Benevento, Italy
* Vito Alessandro Carella - Department of Computer Science, University of Bari Aldo Moro
