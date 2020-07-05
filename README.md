# NetherTree

The Nether update added new trees, why shouldn't their "leaves" decay?

![Demonstration GIF](https://i.imgur.com/IgkRiUU.gif)

## Features

- Non-persistent blocks in the trees are gradually disappearing if they have no stem.
- Supports already generated chunks.
- Differentiates persistent blocks, even if they were placed before installing the plugin.
- Configurable drop rates.

## How does it work?

Unlike the behavior of other leaves, the plugin listens to specific events and computes if the surrounding blocks should disappear. Then a task will make them decay, in the way that randomTickSpeed works.
