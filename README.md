# Protocol 🌐

* **Module:** `protocol`
* **Depends on:** N/A
* **Resources:** The official [Minecraft protocol wiki](https://minecraft.wiki/w/Java_Edition_protocol/Packets) is your
  bible.
  It details every packet format, data type, and the connection state flow (handshake, status, login, play).
* **What you should look at:**
    * **Data Types:** Minecraft uses specific data types like **VarInt** (variable-length integers) and **NBT** (Named
      Binary Tag). You will need to implement functions to read and write these from byte buffers.
    * **Packet Structures:** Each message sent between the client and server is a packet. You'll need to create Kotlin
      data classes that model these packets and a system for mapping packet IDs to their corresponding data classes.
    * **Packet I/O:** Implement a binary reader and writer. This is the core logic that will convert raw byte streams
      into meaningful Kotlin objects and vice versa.