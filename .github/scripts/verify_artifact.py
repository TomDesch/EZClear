from pathlib import Path
import re
import struct
import zipfile

ARTIFACT_GLOB = "EZClear-*.jar"
PLUGIN_PACKAGE = "io/stealingdapenta/ezclear/"
BYTECODE_MAJOR = 61
FORBIDDEN_PACKAGES = (
    "org/bukkit/",
    "io/papermc/",
    "com/destroystokyo/paper/",
    "net/minecraft/",
    "org/junit/",
    "org/opentest4j/",
    "org/apiguardian/",
)
API_VERSION_RE = r"(?m)^api-version:\s*['\"]?1\.20['\"]?\s*$"
MAIN_CLASS = "io.stealingdapenta.ezclear.EZClear"


def require(condition, message):
    if not condition:
        raise RuntimeError(message)


artifacts = [
    path for path in Path("target").glob(ARTIFACT_GLOB)
    if not path.name.startswith("original-")
    and not path.stem.endswith(("-sources", "-javadoc", "-tests"))
]
require(len(artifacts) == 1, f"Expected one deployable JAR, found {artifacts}")
artifact = artifacts[0]

with zipfile.ZipFile(artifact) as jar:
    entries = jar.namelist()
    require("plugin.yml" in entries, "Missing plugin.yml")
    own_classes = 0
    for entry in entries:
        if not entry.endswith(".class"):
            continue
        require(not entry.startswith(FORBIDDEN_PACKAGES), f"Unintended bundled class: {entry}")
        if entry.startswith(PLUGIN_PACKAGE):
            own_classes += 1
            version = struct.unpack(">H", jar.read(entry)[6:8])[0]
            require(version == BYTECODE_MAJOR, f"Unexpected bytecode version {version}: {entry}")
    require(own_classes > 0, "No EZClear classes found in the deployable JAR")
    metadata = jar.read("plugin.yml").decode("utf-8")
    require(re.search(API_VERSION_RE, metadata), "api-version changed without compatibility review")
    require(re.search(rf"(?m)^main:\s*{re.escape(MAIN_CLASS)}\s*$", metadata),
            "plugin.yml main class does not match the plugin entry point")

print(f"Verified deployable artifact: {artifact} ({artifact.stat().st_size:,} bytes)")
