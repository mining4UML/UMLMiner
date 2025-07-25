# UML Miner - To make UML diagrams mining possible

UML Miner is a Visual Paradigm plug-in that makes possible mining all the UML diagrams realized in real-time. 
It makes possible to:
- produce  a Log Event written in the XES format that can be readily explored and analyzed upon the generation.
- export all the Logs generated in the XES format. The XES standard defines a grammar for a tag-based language whose aim is to provide designers of information systems with a unified and extensible methodology for capturing systems behaviors by means of event logs and event streams is defined in the XES standard.
- find out the actual modelling process which is happening inside a software project. To this aim, it allows to execute the [Declare Miner](https://www.sciencedirect.com/science/article/abs/pii/S0306437916306615?via%3Dihub) and [MINERful](https://dl.acm.org/doi/10.1145/2629447) techniques to process discovery.
- figure out if there are any deviations between the actual modelling process and the ideal modelling process (from the model). To this aim, it allows to execute the [Declare Analyzer](https://www.sciencedirect.com/science/article/abs/pii/S0957417416304390?via%3Dihub) and the [Declare Replayer](https://link.springer.com/chapter/10.1007%2F978-3-642-32885-5_6) methods to conformance checking. The conformance can be explored either by trace or by constraint.
- [receive targeted and context-aware feedback through an advanced Retrieval-Augmented Generation (RAG) component based on Large Language Models (LLMs)]. This feature, continuously refined for accuracy and alignment with modeling best practices, supports learners in understanding modeling concepts and making informed design decisions throughout the modeling process.

# How does it work?
For each "Visual Paradigm" project, UML Miner creates a unique log. A log is made up of all the working sessions on that project.
A working session of a project represents all the modeling events made across all the UML diagrams employed between an opening and the corresponding closure of the project.
Modeling events captured are related to:
- adding or removing UML diagrams in a project;
- adding or removing UML model elements in a project;
- adding or removing UML model elements in a diagram;
- adding, changing or removing any property of a model element contained in a project;
- adding, changing or removing any property of a model element contained in a diagram;
- adding, changing or removing any relatiosnhip between two or more UML model elements;
- adding, changing or removing any extension mechanism (stereotypes, constraints, tagged values).

## Installation
UML Miner comes as a zip file. To install it in Visual Paradigm it only necessary to select  "Help > Install Plugin" from the application toolbar. 

## Debugging configuration
Information on how to debug UML Miner in Eclipse IDE are available at https://knowhow.visual-paradigm.com/openapi/debug-plugins-eclipse/

## Development
The development of UML Miner has been started by Ardimento Pasquale and was later continued by Pasquale Ardimento and Vito Alessandro Carella.
First release is going to be published (28th June 2023).

## Team members
The team members, listed in alphabetical order, are:
- Pasquale Ardimento -  Department of Computer Science, University of Bari Aldo Moro (responsible of project)
- Lerina Aversano - Department of Engineering, Università degli Studi del Sannio, 82100 Benevento, Italy
- Mario Luca Bernardi - Department of Engineering, Università degli Studi del Sannio, 82100 Benevento, Italy
- Vito Alessandro Carella - Department of Computer Science, University of Bari Aldo Moro
- Marta Cimitile - Department of Law and Economics, Unitelma Sapienza University, 00161 Rome, Italy
- Michele Scalera - Department of Computer Science, University of Bari Aldo Moro
