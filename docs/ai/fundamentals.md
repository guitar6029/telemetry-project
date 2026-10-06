# AI Fundamentals

These notes give software engineers enough context to reason about AI systems without going deeply into mathematics, calculus, CUDA, PyTorch, or neural-network implementation.

## Concepts

| Term | Practical meaning |
| --- | --- |
| **Artificial intelligence (AI)** | A broad field concerned with systems that perform tasks associated with human intelligence, such as perception, language, planning, or decision-making. |
| **Machine learning (ML)** | A way to build systems whose behavior is learned from examples or data rather than specified entirely as hand-written rules. |
| **Deep learning** | A family of machine-learning methods that uses neural networks with many layers to learn complex patterns. |
| **Generative AI** | AI that produces new content, such as text, images, audio, or code, based on learned patterns and an input. |
| **Large language model (LLM)** | A model trained on large amounts of text and other data to process and generate language-like sequences. It can be used for tasks such as summarization, question answering, and code assistance. |
| **Model** | The learned function and parameters used to turn an input into an output. A model alone is not a complete application or agent. |
| **Parameters** | Learned numeric values inside a model. They encode patterns acquired during training; they are not a dependable, editable database of facts. |
| **Training** | The process of adjusting model parameters using data and an optimization procedure so the model learns useful patterns. |
| **Inference** | Using a trained model to produce an output from an input. This is what happens when an application calls a model at runtime. |
| **Probabilistic output** | Model outputs are generated from learned probability distributions. Similar inputs can produce different outputs, and plausible-sounding output can still be incorrect. |

## Engineering implications

- Model output should be treated as a proposal that may need validation, not as guaranteed truth.
- System behavior depends on more than the model: instructions, context, tools, permissions, and application logic matter too.
- Use deterministic code for deterministic requirements; use a model where interpretation or judgment adds value.
- Test and evaluate the whole application behavior, including its boundaries and failure paths.
