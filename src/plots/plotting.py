import pandas as pd
import matplotlib.pyplot as plt

df = pd.read_csv("../rtt_data.csv")

# === Plot 1: SampleRTT and EstimatedRTT ===
plt.figure(figsize=(8, 6))
plt.plot(df["request_id"], df["sampleRTT"], 'b-*', label="SampleRTT")
plt.plot(df["request_id"], df["estimatedRTT"], 'r-o', label="EstimatedRTT")
plt.xlabel("Number of updates")
plt.ylabel("Time in ms")
plt.title("SampleRTT and EstimatedRTT")
plt.legend()
plt.grid(True)
plt.tight_layout()
plt.savefig("SampleRTT_vs_EstimatedRTT.png")
plt.show()

# === Plot 2: SampleRTT and DevRTT ===
plt.figure(figsize=(8, 6))
plt.plot(df["request_id"], df["sampleRTT"], 'b-*', label="SampleRTT")
plt.plot(df["request_id"], df["devRTT"], 'r-o', label="DevRTT")
plt.xlabel("Number of updates")
plt.ylabel("Time in ms")
plt.title("SampleRTT and DevRTT")
plt.legend()
plt.grid(True)
plt.tight_layout()
plt.savefig("SampleRTT_vs_DevRTT.png")
plt.show()
