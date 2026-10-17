from pathlib import Path
import subprocess
import sys
import tempfile
import unittest
import zipfile

CHECKER = Path(__file__).with_name("verify_artifact.py")
VALID_METADATA = """\
name: EZClear
main: io.stealingdapenta.ezclear.EZClear
api-version: '1.20'
"""
CLASS_HEADER = b"\xCA\xFE\xBA\xBE\x00\x00\x00\x3D"


class VerifyArtifactTest(unittest.TestCase):
    def run_checker(self, extra_entries=None, metadata=VALID_METADATA, class_header=CLASS_HEADER):
        with tempfile.TemporaryDirectory() as directory:
            artifact_dir = Path(directory) / "target"
            artifact_dir.mkdir()
            artifact = artifact_dir / "EZClear-1.0.jar"
            entries = {
                "plugin.yml": metadata.encode("utf-8"),
                "io/stealingdapenta/ezclear/EZClear.class": class_header,
            }
            if extra_entries:
                entries.update(extra_entries)
            with zipfile.ZipFile(artifact, "w") as jar:
                for name, contents in entries.items():
                    jar.writestr(name, contents)
            return subprocess.run(
                [sys.executable, str(CHECKER)],
                cwd=directory,
                capture_output=True,
                text=True,
                check=False,
            )

    def testValidArtifactPasses(self):
        result = self.run_checker()

        self.assertEqual(0, result.returncode, result.stderr)

    def testArtifactWithWrongApiVersionFails(self):
        result = self.run_checker(metadata=VALID_METADATA.replace("'1.20'", "'26.3'"))

        self.assertNotEqual(0, result.returncode)
        self.assertIn("api-version changed", result.stderr)

    def testArtifactWithBundledBukkitClassFails(self):
        result = self.run_checker(extra_entries={"org/bukkit/Bukkit.class": CLASS_HEADER})

        self.assertNotEqual(0, result.returncode)
        self.assertIn("org/bukkit/Bukkit.class", result.stderr)

    def testArtifactWithBundledPaperClassFails(self):
        result = self.run_checker(extra_entries={"io/papermc/paper/Paper.class": CLASS_HEADER})

        self.assertNotEqual(0, result.returncode)
        self.assertIn("io/papermc/paper/Paper.class", result.stderr)

    def testArtifactWithWrongBytecodeVersionFails(self):
        result = self.run_checker(class_header=b"\xCA\xFE\xBA\xBE\x00\x00\x00\x46")

        self.assertNotEqual(0, result.returncode)
        self.assertIn("Unexpected bytecode version", result.stderr)

    def testArtifactWithWrongMainClassFails(self):
        metadata = VALID_METADATA.replace("io.stealingdapenta.ezclear.EZClear", "example.Main")
        result = self.run_checker(metadata=metadata)

        self.assertNotEqual(0, result.returncode)
        self.assertIn("main class does not match", result.stderr)


if __name__ == "__main__":
    unittest.main()
