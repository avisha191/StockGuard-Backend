
import json
import urllib.request
import urllib.error
from concurrent.futures import ThreadPoolExecutor, as_completed

PRODUCT_ID = 2
URL = "http://localhost:8080/api/orders"

def place_order(_):
    request = urllib.request.Request(
        f"{URL}?productId={PRODUCT_ID}&quantity=1",
        method="POST"
    )

    try:
        with urllib.request.urlopen(request, timeout=20) as response:
            return response.status, response.read().decode()
    except urllib.error.HTTPError as error:
        return error.code, error.read().decode()

results = []

with ThreadPoolExecutor(max_workers=10) as executor:
    futures = [executor.submit(place_order, i) for i in range(10)]

    for future in as_completed(futures):
        results.append(future.result())

successes = sum(status == 201 for status, _ in results)
failures = len(results) - successes

print("Successful orders:", successes)
print("Rejected orders:", failures)
print("\nIndividual responses:")

for status, body in results:
    print(status, body)

print("\nExpected: 5 successful orders, 5 rejected orders.")
